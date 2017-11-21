package ru.uniteller;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.ClassReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;
import ru.uniteller.inspector.TestStandInspector;

import java.util.ArrayList;
import java.util.List;

public class SubjectCommand {

    private static final Logger LOG = Logger.getInstance(TestStandInspector.class);
    private static final String SUBJECT_INTERFACE_NAME = "SubjectInterface";
    private static final String COMMAND_INTERFACE_NAME = "CommandInterface";
    private PhpIndex phpIndex;

    public SubjectCommand(PhpIndex phpIndex) {
        this.phpIndex = phpIndex;
    }

    public SubjectCommand(Project project) {
        this.phpIndex = PhpIndex.getInstance(project);
    }

    public PhpClass getInterfaceByName(@NotNull String nameInterface) {
        return this.phpIndex.getInterfacesByName(nameInterface).size() > 0 ? this.phpIndex.getInterfacesByName(nameInterface).toArray(new PhpClass[1])[0] : null;

    }



    public List<PhpClass> getAllClassForSubject(PhpClass phpClass){
        if (!isAncestorSubject(phpClass)){
            return null;
        }
        return new ArrayList<>();
    }

    /**
     * Является ли PHPClass предком SubjectInterface
     *
     * @param phpClass Класс PHP
     * @return {@code true} - если данный класс является предком SubjectInterface
     * TODO UnitTest
     */
    public boolean isAncestorSubject(PhpClass phpClass) {
        return this.isAncestor(phpClass, SUBJECT_INTERFACE_NAME);
    }

    /**
     * Является ли PHPClass предком CommandInterface
     *
     * @param phpClass Класс PHP
     * @return {@code true} - если данный класс является предком CommandInterface
     * TODO UnitTest
     */
    public boolean isAncestorCommand(PhpClass phpClass) {
        return this.isAncestor(phpClass, COMMAND_INTERFACE_NAME);
    }


    private boolean isAncestor(PhpClass phpClass, String nameAncestor) {
        LOG.info(phpClass.getName() + " is " + nameAncestor + "?");

        if (phpClass.isInterface()) {
            for (ClassReference extended : phpClass.getExtendsList().getReferenceElements()) {
                LOG.info("Founded extended Class: " + extended.getName());
                if (extended.getName().equalsIgnoreCase(nameAncestor)) return true;
                if (isAncestor((PhpClass) extended, nameAncestor)) return true;
            }
        } else {
                for (PhpClass implemented : phpClass.getImplementedInterfaces()) {
                LOG.info("Founded implemented Class: " + implemented.getName());
                if (implemented.getName().equalsIgnoreCase(nameAncestor)) return true;
                if (isAncestor(implemented, nameAncestor)) return true;
            }
        }
        return false;
    }


}
