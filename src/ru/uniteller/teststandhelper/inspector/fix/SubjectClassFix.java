package ru.uniteller.teststandhelper.inspector.fix;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.documentation.phpdoc.psi.impl.PhpDocCommentImpl;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SubjectClassFix extends AbstractQuickFix {


    public SubjectClassFix(@NotNull PhpClass phpClass) {
        super(phpClass);
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile psiFile, @Nullable Editor editor, @NotNull PsiElement psiElement, @NotNull PsiElement psiElement1) {
        PhpClass phpClass = getPhpClassByStartElement(psiElement);
        if (phpClass.getDocComment() == null) {
            PhpDocComment phpDocComment = PhpPsiElementFactory.createFromText(phpClass.getProject(), PhpDocCommentImpl.class, "/** @package " + phpClass.getName() + " */");
            phpClass.getParent().addBefore(phpDocComment,phpClass);
        }
    }

    @NotNull
    @Override
    public String getText() {
        return "Fix";
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "";
    }
}
