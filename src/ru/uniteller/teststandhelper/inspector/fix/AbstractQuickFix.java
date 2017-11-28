package ru.uniteller.teststandhelper.inspector.fix;

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractQuickFix extends LocalQuickFixAndIntentionActionOnPsiElement {


    protected AbstractQuickFix(@Nullable PsiElement element) {
        super(element);
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile psiFile, @Nullable Editor editor, @NotNull PsiElement psiElement, @NotNull PsiElement psiElement1) {

    }

    @NotNull
    @Override
    public String getText() {
        return null;
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return null;
    }

    protected final PhpClass getPhpClassByStartElement(PsiElement element) {
        while (!(element instanceof PhpClass)) {
            element = element.getParent();
        }
        return (PhpClass) element;
    }
}
