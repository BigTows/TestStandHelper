package ru.uniteller;

import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;

public class PhpClassAndMethod{
    private PhpClass phpClass;
    private Method method;

    public PhpClassAndMethod(PhpClass phpClass, Method method) {
        this.phpClass = phpClass;
        this.method = method;
    }

    public PhpClass getPhpClass() {
        return phpClass;
    }

    public Method getMethod() {
        return method;
    }
}