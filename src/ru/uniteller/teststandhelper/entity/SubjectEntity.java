package ru.uniteller.teststandhelper.entity;

import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.openapi.diagnostic.Logger;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import ru.uniteller.teststandhelper.exception.TestStandHelperException;
import ru.uniteller.teststandhelper.util.ClassHelper;
import ru.uniteller.teststandhelper.util.Config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SubjectEntity {

    private PhpClass subject;

    public SubjectEntity(PhpClass phpClass) throws TestStandHelperException {
        if (!ClassHelper.isSubjectAncestor(phpClass))
            throw new TestStandHelperException(phpClass.getFQN() + " Is not Subject");
        this.subject = phpClass;
    }

    public String getNameSubject() {
        if (subject.isInterface()) {
            return subject.getName().replace("Interface", "");
        } else {
            return subject.getName();
        }
    }

    public PhpClass getSubject() {
        return subject;
    }

    public String toPhpDoc(){
        StringBuilder builder = new StringBuilder("/** \n");
        for (CommandSubject commandSubject: getCommandSubject()){
            builder.append(commandSubject.toString());
        }
        return builder.append(" */").toString();
    }

    public List<CommandSubject> getCommandSubject(){
        List<CommandSubject> commandSubjects = new ArrayList<>();
        for (PhpClass phpClass : ClassHelper.getClassesSubjectCommand(this)){
            try {
                commandSubjects.add(new CommandSubject(phpClass));
            } catch (TestStandHelperException ignored) {

            }
        }
        return commandSubjects;
    }
}
