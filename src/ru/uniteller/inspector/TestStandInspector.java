package ru.uniteller.inspector;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.PhpIndex;
import org.jetbrains.annotations.NotNull;

public class TestStandInspector extends LocalInspectionTool {
    private static final Logger LOG = Logger.getInstance(TestStandInspector.class);

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {

           PhpIndex phpIndex =  holder.getProject().getComponent(PhpIndex.class);
           for (String nameInterface : phpIndex.getAllInterfaceNames()){

           }

        return super.buildVisitor(holder, isOnTheFly);
    }
}
