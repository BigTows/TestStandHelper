package ru.uniteller.inspector;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocMethod;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.uniteller.MethodCommandLocalQuickFix;
import ru.uniteller.PhpClassAndMethod;
import ru.uniteller.SubjectCommand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestStandInspector extends LocalInspectionTool {
    private static final Logger LOG = Logger.getInstance(TestStandInspector.class);

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {


        return new PsiElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                if (element instanceof PhpClass) {
                    inspectClass((PhpClass) element, holder);
                }
                super.visitElement(element);
            }
        };
        /*SubjectCommand helper = new SubjectCommand(holder.getProject());
        PhpClass phpClass = PsiTreeUtil.getParentOfType(holder.getFile().getContext(), PhpClass.class);

        PsiFile file = holder.getFile();
        for (PsiElement element: file.getChildren()){
            PhpClass phpClass1 = PsiTreeUtil.getParentOfType(element,PhpClass.class);
            if (phpClass1 != null) {
                LOG.info(phpClass1.getName());
            }
        }

        LOG.info(String.valueOf(holder.getFile().getOriginalFile().getName()));
        /*
        PhpIndex.getInstance(holder.getFile().getParent());
        PhpIndex phpIndex = holder.getProject().getComponent(PhpIndex.class);
        for (PhpNamespace namespace : phpIndex.getNamespacesByName("Super")) {

        }
        for (String nameInterface : phpIndex.getAllInterfaceNames()) {
            //TODO RegEx
            if (nameInterface.matches("(.*)subject") && !nameInterface.equalsIgnoreCase("subject")) {
                LOG.info("Interface: " + nameInterface);
                Collection<PhpClass> phpInterface = phpIndex.getInterfacesByName(nameInterface);
                for (PhpClass phpClass : phpInterface) {
                    LOG.info("Class: " + phpClass.toString() + " is Interface: " + phpClass.isInterface());
                    //TODO Out if Bounds
                    LOG.info("Parent: " + phpClass.getSupers()[0].toString());
                }
            }
        }*/

        //return super.buildVisitor(holder, isOnTheFly);
    }

    private void inspectClass(PhpClass phpClass, ProblemsHolder holder) {
        SubjectCommand sc = new SubjectCommand(phpClass.getProject());
        if (!phpClass.isInterface() || !sc.isAncestorSubject(phpClass)) {
            //TODO Remove this
            LOG.info("Class: " + phpClass.getName() + ".    \nINFO: isInterface: " + phpClass.isInterface() + ", is Ancestor Subject: " + sc.isAncestorSubject(phpClass));
            return;
        }

       /* for (Map.Entry<String, PhpClassAndMethod> entry : sc.getSchema(phpClass).entrySet()){
            LOG.info("\nCommand name: "+entry.getKey()+"\n"+
            "Class: "+entry.getValue().getPhpClass().getName()+"\n"+
            "MethodFull: "+entry.getValue().getMethod().getName());
        }*/
        checkAnnotation(holder, sc.getSchema(phpClass), phpClass);


    }


    private void checkAnnotation(ProblemsHolder holder, Map<String, PhpClassAndMethod> methodEntry, PhpClass phpClass) {
        PhpDocMethod[] methods = phpClass.getDocComment().getMethods();
        if (methods!=null && methods.length==0) return;
        for (PhpDocMethod docMethod : phpClass.getDocComment().getMethods()) {
            PhpClassAndMethod classAndMethod = methodEntry.get(docMethod.getName());
            if (classAndMethod == null) {
                PsiElement firstChild = docMethod.getFirstChild();
                holder.registerProblem(
                        firstChild,
                        "Undefined method",
                        ProblemHighlightType.ERROR,
                        new MethodCommandLocalQuickFix(docMethod)
                );
            }else{
                methodEntry.values().remove(classAndMethod);
            }
        }
        if (methodEntry.size()!=0){
            holder.registerProblem(phpClass.getFirstChild().getChildren()[1],"Not all method's init",ProblemHighlightType.ERROR,new MethodCommandLocalQuickFix(phpClass));
        }
    }

}
