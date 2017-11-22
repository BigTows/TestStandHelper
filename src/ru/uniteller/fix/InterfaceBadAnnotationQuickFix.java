package ru.uniteller.fix;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.uniteller.PhpClassAndMethod;

import java.util.Map;

public class InterfaceBadAnnotationQuickFix extends LocalQuickFixAndIntentionActionOnPsiElement {
    private Map<String, PhpClassAndMethod> missingMethod;
    private PhpClass phpClass;

    public InterfaceBadAnnotationQuickFix(@NotNull PhpClass phpClass, Map<String, PhpClassAndMethod> methodMap) {
        super(phpClass);
        this.phpClass = phpClass;
        this.missingMethod = methodMap;
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file, @Nullable Editor editor, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        if (editor == null) return;
        if (phpClass.getDocComment() == null)
            return;
        if (phpClass.getDocComment().getMethods().length == 0) {
            HintManager.getInstance().showErrorHint(editor, "Ops, nothing found");
            return;
        }
        //TODO
    }


    private void invoke() {


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
