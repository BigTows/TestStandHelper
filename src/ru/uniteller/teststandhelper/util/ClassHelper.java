package ru.uniteller.teststandhelper.util;

import com.jetbrains.php.lang.psi.elements.PhpClass;

public class ClassHelper {

    private static LogHelper logHelper = new LogHelper(ClassHelper.class);

    public static boolean isCommandAncestor(PhpClass phpClass) {
        return isAncestor(phpClass, Config.NAME_COMMAND_INTERFACE);
    }
    public static boolean isSubjectAncestor(PhpClass phpClass) {
        return isAncestor(phpClass, Config.NAME_SUBJECT_INTERFACE);
    }

    /**
     * Является ли PhpClass предком {@code nameAncestor}
     *
     * @param phpClass     Php класс
     * @param nameAncestor имя PhpClass
     * @return {@code true} Если phpClass является предком {@code nameAncestor}
     * @see PhpClass
     */
    public static boolean isAncestor(PhpClass phpClass, String nameAncestor) {
        logHelper.i(phpClass.getName() + " is ancestor" + nameAncestor + "?");

        if (!phpClass.isInterface()) {
            for (PhpClass extended : phpClass.getSupers()) {
                logHelper.i("  Founded extended Class: " + extended.getName());
                if (extended.getName().equalsIgnoreCase(nameAncestor)) return true;
                if (isAncestor(extended, nameAncestor)) return true;
            }
        } else {
            for (PhpClass implemented : phpClass.getImplementedInterfaces()) {
                logHelper.i("  Founded implemented Class: " + implemented.getName());
                if (implemented.getName().equalsIgnoreCase(nameAncestor)) return true;
                if (isAncestor(implemented, nameAncestor)) return true;
            }
        }
        return false;
    }

}
