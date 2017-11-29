package ru.uniteller.teststandhelper.inspector;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.documentation.phpdoc.psi.impl.PhpDocCommentImpl;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;
import ru.uniteller.teststandhelper.entity.CommandSubject;
import ru.uniteller.teststandhelper.entity.Subject;
import ru.uniteller.teststandhelper.entity.SubjectEntity;
import ru.uniteller.teststandhelper.exception.TestStandHelperException;
import ru.uniteller.teststandhelper.repository.SubjectRepository;
import ru.uniteller.teststandhelper.util.LogHelper;

public class InterfaceSubjectInspector extends LocalInspectionTool {
    private LogHelper log = new LogHelper(getClass());

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {


        return new PsiElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                if (element instanceof PhpClass) {
                    try {
                        inspectClass((PhpClass) element, holder);
                    } catch (TestStandHelperException e) {
                        e.printStackTrace();
                    }
                }
                super.visitElement(element);
            }
        };
    }


    private void inspectClass(PhpClass phpClass, ProblemsHolder holder) throws TestStandHelperException {
        SubjectEntity subjectEntity = new SubjectEntity(phpClass);
        log.i("\n"+subjectEntity.toPhpDoc());

        /*holder.registerProblem(
                phpClass.getFirstChild(),
                "Неизвестный метод",
                ProblemHighlightType.ERROR,
                new SubjectClassFix(phpClass)
        );*/

    }

    private void initPhpDoc(PhpClass phpClass) {
        if (phpClass.getDocComment() == null) {
            PhpDocComment phpDocComment = PhpPsiElementFactory.createFromText(phpClass.getProject(), PhpDocCommentImpl.class, "/** @package " + phpClass.getName() + " */");
            phpClass.getParent().getParent().addBefore(phpDocComment, phpClass.getParent());
            //  phpClass.getParent().add(phpDocComment);

        }
    }


}
