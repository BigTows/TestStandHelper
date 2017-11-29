package ru.uniteller.teststandhelper.util;

import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocType;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.Parameter;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import ru.uniteller.teststandhelper.entity.SubjectEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClassHelper {

    private static LogHelper logHelper = new LogHelper(ClassHelper.class);
    private static final String LOCAL_TYPE_DOMAIN_INTERFACE = "\\TestSrv\\Lib\\Domain\\DomainInterface";
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
        logHelper.d(phpClass.getName() + " is ancestor" + nameAncestor + "?");

        if (!phpClass.isInterface()) {
            for (PhpClass extended : phpClass.getSupers()) {
                logHelper.d("  Founded extended Class: " + extended.getName());
                if (extended.getName().equalsIgnoreCase(nameAncestor)) return true;
                if (isAncestor(extended, nameAncestor)) return true;
            }
        } else {
            for (PhpClass implemented : phpClass.getImplementedInterfaces()) {
                logHelper.d("  Founded implemented Class: " + implemented.getName());
                if (implemented.getName().equalsIgnoreCase(nameAncestor)) return true;
                if (isAncestor(implemented, nameAncestor)) return true;
            }
        }
        return false;
    }

    /**
     * Являели ли данный метод "Командным" (не абстрактный,публичный, не статичный,
     * первый аргумент метода, должен быть \TestSrv\Lib\Domain\DomainInterface,
     * или наследоваться от него.)
     *
     * @param method Метод
     * @return {@code true} Если данный метод является "Командным"
     * @see Method
     */
    public static boolean isValidCommandMethod(Method method) {
        return isValidSignatureMethod(method) && !method.isAbstract() && !method.isStatic() && method.getAccess().isPublic();
    }

    /**
     * Является ли сингнатура метода валидной (
     *
     * @param method Метод класса
     * @return {@code true} Если данный метод имеет валидную Сигнатуру
     * @see Method
     */
    private static boolean isValidSignatureMethod(Method method) {
        Parameter parameters[] = method.getParameters();
        if (parameters.length == 0) return false;
        String nameSpaceType = parameters[0].getDeclaredType().toString();
        if (nameSpaceType.equals(LOCAL_TYPE_DOMAIN_INTERFACE))
            return true;
        PhpIndex phpIndex = PhpIndex.getInstance(method.getProject());
        Collection<PhpClass> parametersClass =phpIndex.getClassesByName(nameSpaceType);
        Collection<PhpClass> parametersInterface = phpIndex.getInterfacesByFQN(nameSpaceType);
        if (parametersClass.size() == 1) {
            for (PhpClass phpClass : parametersClass)
                return isAncestor(phpClass, "DomainInterface");
        } else if (parametersInterface.size() == 1) {
            for (PhpClass phpClass : parametersInterface)
                return isAncestor(phpClass, "DomainInterface");
        }
        return false;
    }

    public static List<PhpClass> getClassesSubjectCommand(SubjectEntity subjectEntity) {
        List<PhpClass> classes = new ArrayList<>();
        logHelper.d("Check commands for "+subjectEntity.getNameSubject());
        PhpIndex phpIndex = PhpIndex.getInstance(subjectEntity.getSubject().getProject());
        for (String phpClassName : phpIndex.getAllClassNames(PrefixMatcher.ALWAYS_TRUE)) {
            classes.addAll(phpIndex.getClassesByFQN(
                    Config.NAMESPACE_SUBJECT +
                            subjectEntity.getNameSubject() +
                            Config.NAMESPACE_COMMAND + phpClassName
            ));
        }
        logHelper.d("Founded: "+classes.size());
        return classes;
    }

    public static List<PhpClass> getClassesSubjectByFQN(PhpIndex phpIndex, String type) {
        List<PhpClass> classes = new ArrayList<>();
        if (type.length() > 0) type = "\\" + type;
        for (String phpClassName : phpIndex.getAllClassNames(PrefixMatcher.ALWAYS_TRUE)) {
            classes.addAll(phpIndex.getClassesByFQN(Config.NAMESPACE_SUBJECT + phpClassName + type));
        }
        return classes;
    }


    public static String getReturnTypeFromMethod(Method method) {
        if (method.getDocComment() == null){
            return "$this";
        }
        if (method.getDocComment().getReturnTag()==null){
            return "$this";
        }
        for (PsiElement element : method.getDocComment().getReturnTag().getChildren()){
            if (element instanceof PhpDocType){
                return ((PhpDocType) element).getName();
            }
        }
        return "$this";
    }


    public static String methodParamsSubjectToString(Method method) {
        Parameter[] parameter = method.getParameters();
        StringBuilder signature = new StringBuilder("(");
        for (int i = 1; i < parameter.length; i++) {
            signature.append(parameter[i].getDeclaredType().toString()).
                    append(" $").
                    append(parameter[i].getName()).
                    append(", ");
        }
        if (signature.length()>2) {
            signature.deleteCharAt(signature.length() - 1);
            signature.deleteCharAt(signature.length() - 1);
        }
        return signature.append(")").toString();
    }

}
