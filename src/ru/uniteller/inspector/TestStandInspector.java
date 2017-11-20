package ru.uniteller.inspector;

import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.ClassReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamespace;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class TestStandInspector extends LocalInspectionTool {
    private static final Logger LOG = Logger.getInstance(TestStandInspector.class);

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {

        PhpIndex phpIndex = holder.getProject().getComponent(PhpIndex.class);
        for (PhpNamespace namespace:phpIndex.getNamespacesByName("Super")){

        }
        for (String nameInterface : phpIndex.getAllInterfaceNames()) {
            //TODO RegEx
            if (nameInterface.matches("(.*)subject") && !nameInterface.equalsIgnoreCase("subject")) {
                LOG.info("Interface: "+nameInterface);
                Collection<PhpClass> phpInterface = phpIndex.getInterfacesByName(nameInterface);
                for (PhpClass phpClass : phpInterface) {
                    LOG.info("Class: "+phpClass.toString()+" is Interface: "+phpClass.isInterface());
                    //TODO Out if Bounds
                    LOG.info("Parent: "+phpClass.getSupers()[0].toString());
                }
            }
        }

        return super.buildVisitor(holder, isOnTheFly);
    }
}
