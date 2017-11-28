package ru.uniteller.teststandhelper.entity;

import com.jetbrains.php.lang.psi.elements.PhpClass;

public class CommandSubject {

    private PhpClass phpClass;

    public CommandSubject(PhpClass phpClass) {
        this.phpClass = phpClass;
    }


    public PhpClass getPhpClass() {
        return phpClass;
    }

    /**
     *
     * @param phpClass Класс-Команда
     * @return {@code true} Если
     * @see CommandSubject
     */
    public static boolean isCommandSubject(PhpClass phpClass){
        return false;
    }
}
