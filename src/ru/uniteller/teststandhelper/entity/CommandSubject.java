package ru.uniteller.teststandhelper.entity;

import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import ru.uniteller.teststandhelper.exception.TestStandHelperException;
import ru.uniteller.teststandhelper.util.ClassHelper;

import java.util.ArrayList;
import java.util.List;

public class CommandSubject {

    private PhpClass phpClass;

    public CommandSubject(PhpClass phpClass) throws TestStandHelperException {
        if (!ClassHelper.isCommandAncestor(phpClass))
            throw new TestStandHelperException(phpClass.getFQN() + " is not Command");
        this.phpClass = phpClass;
    }

    public String getNameCommand() {
        return phpClass.getName().replace("Command", "");
    }

    public PhpClass getPhpClass() {
        return phpClass;
    }

    /**
     * @param phpClass Класс-Команда
     * @return {@code true} Если
     * @see CommandSubject
     */
    public static boolean isCommandSubject(PhpClass phpClass) {
        return false;
    }


    public List<Method> getMethodSubject() {
        List<Method> methods = new ArrayList<>();
        for (Method method : phpClass.getMethods()) {
            if (ClassHelper.isValidCommandMethod(method))
                methods.add(method);
        }
        return methods;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        //TODO Ask About Return Type
        for (Method method : getMethodSubject()) {
            builder.append(" * @method ").append(ClassHelper.getReturnTypeFromMethod(method)).append(" ").append(method.getName()).append(getNameCommand());
            builder.append(ClassHelper.methodParamsSubjectToString(method)).append("\n");
            builder.append(" * @see ").append(method.getFQN().replace(".","::"));
            builder.append("\n *\n");
        }
        return builder.toString();
    }
}
