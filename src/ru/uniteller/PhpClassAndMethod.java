package ru.uniteller;

import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.Parameter;
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

    @Override
    public String toString() {
        Parameter[] parameter = getMethod().getParameters();
        StringBuilder signature = new StringBuilder("(");
        for (int i =1;i<parameter.length;i++){
            signature.append(parameter[i].getDeclaredType().toString()).
                    append(" $").
                    append(parameter[i].getName()).
                    append(", ");
        }
        signature.deleteCharAt(signature.length()-1);
        signature.deleteCharAt(signature.length()-1);
        return signature.append(")").toString();
    }
}