package ru.uniteller.teststandhelper.inspector.fix;

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocMethodTag;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocType;
import com.jetbrains.php.lang.documentation.phpdoc.psi.impl.PhpDocMethodTagImpl;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.uniteller.teststandhelper.PhpClassAndMethod;

import java.util.Map;

import static com.jetbrains.php.lang.psi.PhpPsiElementFactory.createFromText;

public class InterfaceBadAnnotationQuickFix extends LocalQuickFixAndIntentionActionOnPsiElement {
    private Map<String, PhpClassAndMethod> missingMethod;
    private String namePhpClass;

    public InterfaceBadAnnotationQuickFix(@NotNull PhpClass phpClass, Map<String, PhpClassAndMethod> methodMap) {
        super(phpClass);
        namePhpClass = phpClass.getName();
        this.missingMethod = methodMap;
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file, @Nullable Editor editor, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        PhpClass phpClass = this.getPhpClassByStartElement(startElement);
        if (editor == null) return;
        while (!(startElement instanceof PhpClass)) {
            startElement = startElement.getParent();
        }
        PhpDocComment phpDoc = phpClass.getDocComment();

        if (phpDoc == null) {
            phpClass.getParent().addBefore(PhpPsiElementFactory.createFromText(phpClass.getProject(), PhpDocComment.class, "/**\n*/"), phpClass);
            addMethodToPhpDoc(phpClass.getDocComment());
        } else if (phpDoc.getMethods().length == 0) {
            addMethodToPhpDoc(phpDoc);
        } else {
            addMethodToPhpDoc(phpDoc.getMethods()[phpDoc.getMethods().length - 1]);
        }


    }


    private PhpClass getPhpClassByStartElement(PsiElement element) {
        while (!(element instanceof PhpClass)) {
            element = element.getParent();
        }
        return (PhpClass) element;
    }


    private void addMethodToPhpDoc(PsiElement after) {
        LOG.info(after.toString());
        for (Map.Entry<String, PhpClassAndMethod> entry : missingMethod.entrySet()) {
            PhpDocMethodTag methodTag = createFromText(after.getProject(), PhpDocMethodTagImpl.class,
                    "/** @method " + getReturnTypeFromMethod(entry.getValue().getMethod()) + " " + entry.getKey() + entry.getValue().toString() + "*/");
            if (methodTag != null)
                this.addPhpDocMethod(after, methodTag);
        }
        reformatJavaDoc(after);
    }

    private String getReturnTypeFromMethod(Method method) {
        if (method.getDocComment() == null){
            return "$this";
        }
        if (method.getDocComment().getReturnTag()==null){
            return "$this";
        }
        for (PsiElement element : method.getDocComment().getReturnTag().getChildren()){
            if (element instanceof PhpDocType){
                return ((PhpDocType) element).getName();
            }
        }
        return "$this";
    }

    private void addPhpDocMethod(PsiElement after, @NotNull PhpDocMethodTag methodTag) {
        PsiElement enter = createFromText(after.getProject(), PsiWhiteSpaceImpl.class, "");
        PsiElement space = createFromText(after.getProject(), PsiElement.class, " * ");
        PsiElement leafPsiElement = createFromText(after.getProject(), LeafPsiElement.class, "*");
        assert enter != null && space != null && leafPsiElement != null;
        after.addAfter(enter, after.getChildren()[0]);
        after.addAfter(leafPsiElement, after.getChildren()[1]);
        after.addAfter(space, after.getChildren()[2]);
        after.addAfter(methodTag, after.getChildren()[3]);
    }

    private void reformatJavaDoc(PsiElement theElement) {
        LOG.info("Formated");
        CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(theElement.getProject());
        try {
            codeStyleManager.reformatText(theElement.getContainingFile(), 0, theElement.getContainingFile().getTextLength());
        } catch (IncorrectOperationException e) {
            LOG.info("Could not reformat javadoc since cannot find required elements", e);
        }
    }

    @NotNull
    @Override
    public String getText() {
        return "Добавить методы (" + missingMethod.size() + ")";
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "Добавить";
    }

}
