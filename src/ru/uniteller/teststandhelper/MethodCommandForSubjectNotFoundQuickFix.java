package ru.uniteller.teststandhelper;

import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocMethod;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MethodCommandForSubjectNotFoundQuickFix extends LocalQuickFixAndIntentionActionOnPsiElement {

    public MethodCommandForSubjectNotFoundQuickFix(@Nullable PsiElement element) {
        super(element);
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file, @Nullable Editor editor, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        startElement.getParent().delete();
    }

    @NotNull
    @Override
    public String getText() {
        return "Удалить";
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "TODO";
    }
}
