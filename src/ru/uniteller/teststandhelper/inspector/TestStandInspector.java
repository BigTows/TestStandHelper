package ru.uniteller.teststandhelper.inspector;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocMethod;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;
import ru.uniteller.teststandhelper.MethodCommandForSubjectNotFoundQuickFix;
import ru.uniteller.teststandhelper.PhpClassAndMethod;
import ru.uniteller.teststandhelper.SubjectCommand;
import ru.uniteller.teststandhelper.fix.InterfaceBadAnnotationQuickFix;

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
        if (!phpClass.isInterface()) return;
        if (!sc.isAncestorSubject(phpClass)) return;
        checkAnnotation(holder, sc.getSchemaForSubject(phpClass), phpClass);
    }


    private void checkAnnotation(ProblemsHolder holder, Map<String, PhpClassAndMethod> methodEntry, PhpClass phpClass) {
        if (phpClass.getDocComment() == null) return;
        PhpDocMethod[] methods = phpClass.getDocComment().getMethods();
        if (methods.length == 0) return;
        for (PhpDocMethod docMethod : phpClass.getDocComment().getMethods()) {
            PhpClassAndMethod classAndMethod = methodEntry.get(docMethod.getName());
            if (classAndMethod == null) {
                holder.registerProblem(
                        docMethod.getParent(),
                        "Неизвестный метод",
                        ProblemHighlightType.ERROR,
                        new MethodCommandForSubjectNotFoundQuickFix(docMethod)
                );
            } else {
                methodEntry.values().remove(classAndMethod);
            }
        }
        if (!methodEntry.isEmpty()) {
            StringBuilder buffer = new StringBuilder("В аннотации не найдены методы: \n");
            for (Map.Entry<String, PhpClassAndMethod> entry : methodEntry.entrySet()) {
                LOG.info(entry.getValue().toString());
                buffer.append(entry.getValue().getPhpClass().getFQN()).
                        append("::").
                        append(entry.getValue().getMethod().getName()).
                        append(", \n");
            }
            buffer.deleteCharAt(buffer.length() - 3);  //remove ","
            if (phpClass.getNameIdentifier() != null)
                holder.registerProblem(phpClass.getNameIdentifier(), buffer.toString(), ProblemHighlightType.ERROR, new InterfaceBadAnnotationQuickFix(phpClass, methodEntry));
        }
    }

}
