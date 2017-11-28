package ru.uniteller.teststandhelper.entity;

import com.jetbrains.php.lang.psi.elements.PhpClass;
import ru.uniteller.teststandhelper.exception.TestStandHelperException;
import ru.uniteller.teststandhelper.util.ClassHelper;

import java.util.List;

import static ru.uniteller.teststandhelper.util.Config.NAME_SUBJECT_INTERFACE;

public class SubjectEntity {
    private PhpClass classSubject;
    private List<CommandSubject> commands;

    public SubjectEntity(PhpClass classSubject, List<CommandSubject> commands) throws TestStandHelperException {
        if (!isSubject(classSubject)) throw new TestStandHelperException(classSubject.toString()+" is not Subject");
        this.classSubject = classSubject;
        this.commands = commands;
    }

    public PhpClass getClassSubject() {
        return classSubject;
    }

    public List<CommandSubject> getCommands() {
        return commands;
    }


    public static boolean isSubject(PhpClass phpClass){
       return ClassHelper.isAncestor(phpClass,NAME_SUBJECT_INTERFACE);
    }



}
