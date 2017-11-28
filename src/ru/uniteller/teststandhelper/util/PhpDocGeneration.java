package ru.uniteller.teststandhelper.util;


import com.jetbrains.php.lang.psi.elements.Method;

public class PhpDocGeneration {

    private StringBuffer buffer = new StringBuffer("/**\n");

    public PhpDocGeneration(){

    }

    public PhpDocGeneration appendMethod(){
        buffer.append("* @method ");
        //buffer.append(method.getDocComment());
        buffer.append("\n");
        return this;
    }




}
