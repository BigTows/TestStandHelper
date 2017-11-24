package ru.uniteller.teststandhelper.util;

import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.*;
import org.jetbrains.annotations.NotNull;
import ru.uniteller.teststandhelper.PhpClassAndMethod;
import ru.uniteller.teststandhelper.inspector.TestStandInspector;

import java.util.*;

public class SubjectHelper {

    private static final Logger LOG = Logger.getInstance(TestStandInspector.class);
    private static final String SUBJECT_INTERFACE_NAME = "SubjectInterface";
    private static final String COMMAND_INTERFACE_NAME = "CommandInterface";
    private static final String LOCAL_TYPE_DOMAIN_INTERFACE = "\\TestSrv\\Lib\\Domain\\DomainInterface";
    private PhpIndex phpIndex;

    public SubjectHelper(PhpIndex phpIndex) {
        this.phpIndex = phpIndex;
    }

    public SubjectHelper(Project project) {
        this.phpIndex = PhpIndex.getInstance(project);
    }

    public PhpClass getInterfaceByName(@NotNull String nameInterface) {
        return this.phpIndex.getInterfacesByName(nameInterface).size() > 0 ? this.phpIndex.getInterfacesByName(nameInterface).toArray(new PhpClass[1])[0] : null;
    }

    /**
     * Получаем список всех возможных методов Subject для команд
     *
     * @param interfaceSubject Интерфейс Субъекта
     * @return Список команд субъекта (МетодСубъекта =  имяМетода+ИмяКоманды).
     * Возвращаемая структура это Map ключем является методСубъекта, а значением является Класс
     * {@code PhpClassAndMethod}
     * @see PhpClassAndMethod
     */
    public Map<String, PhpClassAndMethod> getSchemaForSubject(PhpClass interfaceSubject) {
        Map<String, PhpClassAndMethod> schema = new HashMap<>();
        List<PhpClass> listCommands = getAllCommandClassForSubject(interfaceSubject);
        if (listCommands == null) return schema;
        for (PhpClass commandClass : listCommands) {
            for (Method method : getMethodsByCommandClass(commandClass)) {
                schema.put(method.getName() + commandClass.getName().split("Command")[0], new PhpClassAndMethod(commandClass, method));
            }
        }
        return schema;
    }


    public List<String> getMethodDomain() {
        List<String> methodList = new ArrayList<>();
        //TODO Sub Interface
        for (PhpClass phpClass : phpIndex.getInterfacesByFQN(LOCAL_TYPE_DOMAIN_INTERFACE)) {
            for (Method method : phpClass.getMethods()) {
                methodList.add(method.getName());
            }
        }
        return methodList;
    }

    private List<PhpClass> getAllCommandClassForSubject(PhpClass subjectClass) {
        if (!isAncestorSubject(subjectClass)) {
            return null;
        }
        List<PhpClass> classes = new ArrayList<>();
        String nameSpace = "\\TestSrv\\Subject\\" + subjectClass.getName().split("Interface")[0] + "\\Command\\";
        //TODO PrefixMatcher (*)Command
        for (String namePhpClass : phpIndex.getAllClassNames(PrefixMatcher.ALWAYS_TRUE)) {
            Collection<PhpClass> col = phpIndex.getClassesByFQN(nameSpace + namePhpClass);
            int count = col.size();
            if (count > 0) {
                PhpClass phpClass = col.iterator().next();
                if (isClass(phpClass) && isAncestorCommand(phpClass))
                    classes.add(phpClass);
            }
        }

        return classes;
    }

    /**
     * Получение всех методов команды
     *
     * @param commandPhpClass Командный класс
     * @return {@code List<Method>} Лист команд
     * @see Method
     * @see PhpClass
     */
    private List<Method> getMethodsByCommandClass(PhpClass commandPhpClass) {
        List<Method> methodsCommand = new ArrayList<>();
        for (Method method : commandPhpClass.getMethods()) {
            if (isValidCommandMethod(method))
                methodsCommand.add(method);
        }
        return methodsCommand;
    }


    /**
     * Является ли PHPClass предком SubjectInterface
     *
     * @param phpClass Класс PHP
     * @return {@code true} - если данный класс является предком SubjectInterface
     * @see PhpClass
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
     * @see PhpClass
     * TODO UnitTest
     */
    public boolean isAncestorCommand(PhpClass phpClass) {
        return this.isAncestor(phpClass, COMMAND_INTERFACE_NAME);
    }

    /**
     * Является ли PhpClass предком {@code nameAncestor}
     *
     * @param phpClass     Php класс
     * @param nameAncestor имя PhpClass
     * @return {@code true} Если phpClass является предком {@code nameAncestor}
     * @see PhpClass
     */
    private boolean isAncestor(PhpClass phpClass, String nameAncestor) {
        LOG.debug(phpClass.getName() + " is " + nameAncestor + "?");

        if (!phpClass.isInterface()) {
            for (PhpClass extended : phpClass.getSupers()) {
                LOG.debug("  Founded extended Class: " + extended.getName());
                if (extended.getName().equalsIgnoreCase(nameAncestor)) return true;
                if (isAncestor(extended, nameAncestor)) return true;
            }
        } else {
            for (PhpClass implemented : phpClass.getImplementedInterfaces()) {
                LOG.debug("  Founded implemented Class: " + implemented.getName());
                if (implemented.getName().equalsIgnoreCase(nameAncestor)) return true;
                if (isAncestor(implemented, nameAncestor)) return true;
            }
        }
        return false;
    }


    /**
     * Является ли переданный класс, обычным(не абстрактным, не интерфейс и не трейт)
     *
     * @param phpClass Класс
     * @return {@code true} Если класс является обычным
     * @see PhpClass
     */
    private boolean isClass(PhpClass phpClass) {
        return !phpClass.isInterface() && !phpClass.isAbstract() && !phpClass.isTrait();
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
    private boolean isValidCommandMethod(Method method) {
        return isValidSignatureMethod(method) && !method.isAbstract() && !method.isStatic() && method.getAccess().isPublic();
    }

    /**
     * Является ли сингнатура метода валидной (
     *
     * @param method Метод класса
     * @return {@code true} Если данный метод имеет валидную Сигнатуру
     * @see Method
     */
    private boolean isValidSignatureMethod(Method method) {
        Parameter parameters[] = method.getParameters();
        if (parameters.length == 0) return false;
        String nameSpaceType = parameters[0].getDeclaredType().toString();
        if (nameSpaceType.equals(LOCAL_TYPE_DOMAIN_INTERFACE))
            return true;
        Collection<PhpClass> parametersClass = phpIndex.getClassesByName(nameSpaceType);
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


}
