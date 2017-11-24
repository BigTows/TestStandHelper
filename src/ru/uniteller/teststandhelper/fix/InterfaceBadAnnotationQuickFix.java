package ru.uniteller.fix;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl;
import com.intellij.psi.tree.IElementType;
import com.jetbrains.php.lang.documentation.phpdoc.psi.impl.PhpDocMethodTagImpl;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.uniteller.PhpClassAndMethod;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.jetbrains.php.lang.psi.PhpPsiElementFactory.createFromText;

public class InterfaceBadAnnotationQuickFix extends LocalQuickFixAndIntentionActionOnPsiElement {
    private Map<String, PhpClassAndMethod> missingMethod;


    private final AtomicReference<PsiWhiteSpaceImpl> enterPsiElement = new AtomicReference<>();
    private final AtomicReference<PsiElement> spacePsiElement = new AtomicReference<>();
    private final AtomicReference<LeafPsiElement> leafPsiElement = new AtomicReference<>();

    public InterfaceBadAnnotationQuickFix(@NotNull PhpClass phpClass, Map<String, PhpClassAndMethod> methodMap) {
        super(phpClass);
        this.missingMethod = methodMap;
        initialize(phpClass.getProject());
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file, @Nullable Editor editor, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        PhpClass phpClass = this.getPhpClassByStartElement(startElement);
        if (editor == null) return;
        while (!(startElement instanceof PhpClass)) {
            startElement = startElement.getParent();
        }
        LOG.info(((PhpClass) startElement).getName());
        if (phpClass.getDocComment() == null)
            return;
        if (phpClass.getDocComment().getMethods().length == 0) {
            HintManager.getInstance().showErrorHint(editor, "Ops, nothing found");
            return;
        }
        addMethodToPhpDoc(phpClass.getDocComment().getMethods()[0]);

    }

    private void initialize(Project project) {
        this.enterPsiElement.set(createFromText(project, PsiWhiteSpaceImpl.class, ""));
        this.spacePsiElement.set(createFromText(project, PsiElement.class," * "));
        this.leafPsiElement.set(createFromText(project, LeafPsiElement.class, "*"));
    }

    private PhpClass getPhpClassByStartElement(PsiElement element) {
        while (!(element instanceof PhpClass)) {
            element = element.getParent();
        }
        return (PhpClass) element;
    }


    private void addMethodToPhpDoc(PsiElement after) {
        if (after == null) return;
        for (Map.Entry<String, PhpClassAndMethod> entry : missingMethod.entrySet()) {
            PhpDocMethodTagImpl methodTag = createFromText(after.getProject(), PhpDocMethodTagImpl.class,
                    "/** @method " + entry.getValue().getPhpClass().getFQN() + " " + entry.getKey() + entry.getValue().toString() + "*/");
            assert methodTag != null;
            after.add(getEnter());
            after.add(getSpace());
            after.add(methodTag);
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

    private PsiElement getSpace() {
        return spacePsiElement.get();
    }

    private PsiWhiteSpaceImpl getEnter() {
        return enterPsiElement.get();
    }

    private LeafPsiElement getLeaf() {
        return leafPsiElement.get();
    }
}
