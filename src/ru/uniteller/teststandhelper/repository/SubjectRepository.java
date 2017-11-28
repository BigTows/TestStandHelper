package ru.uniteller.teststandhelper.repository;

import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import ru.uniteller.teststandhelper.entity.CommandSubject;
import ru.uniteller.teststandhelper.entity.SubjectEntity;
import ru.uniteller.teststandhelper.exception.TestStandHelperException;
import ru.uniteller.teststandhelper.util.ClassHelper;
import ru.uniteller.teststandhelper.util.Config;
import ru.uniteller.teststandhelper.util.LogHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SubjectRepository extends AbstractRepository<SubjectEntity> {
    private LogHelper log = new LogHelper(getClass());

    public SubjectRepository(Project project) {
        super(project);
        this.log.i(project.getName() + " init Repository");
    }


    @Override
    public void update() {
        this.remove();
        for (PhpClass phpClass : getAllSubject(getPhpIndex())) {
            try {
                this.add(new SubjectEntity(phpClass, this.getAllCommandForSubject(phpClass)));
            } catch (TestStandHelperException e) {
                this.log.e(e.getMessage());
            }
        }
    }

    private List<PhpClass> getAllSubject(PhpIndex phpIndex) {
        List<PhpClass> subjectPhpClass = new ArrayList<>();
        for (String phpClassName : phpIndex.getAllClassNames(PrefixMatcher.ALWAYS_TRUE)) {
            Collection<PhpClass> phpClasses = phpIndex.getClassesByFQN(Config.NAMESPACE_SUBJECT + phpClassName);
            if (phpClasses.size() == 0) continue;
            for (PhpClass phpClass : phpClasses) {
                if (ClassHelper.isSubjectAncestor(phpClass)) {
                    subjectPhpClass.add(phpClass);
                }
            }
        }
        return subjectPhpClass;
    }

    private List<CommandSubject> getAllCommandForSubject(PhpClass phpClass) {
        List<CommandSubject> commandSubjects = new ArrayList<>();
        Logger.getInstance(getClass()).info("Look up "+phpClass.getFQN()+"\\Command\\");
        for(PhpClass phpClassCommand: getPhpIndex().getClassesByFQN(phpClass.getFQN()+"\\Command\\")){
            Logger.getInstance(getClass()).info(phpClassCommand.getName()+" + ");
            if (ClassHelper.isCommandAncestor(phpClassCommand)){
                commandSubjects.add(new CommandSubject(phpClassCommand));
            }
        }
        return commandSubjects;
    }
}
