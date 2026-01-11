package sumdu.edu.ua.architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.domain.JavaClasses;
import org.junit.jupiter.api.Test;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class RepositoryLocationTest {

    @Test
    void repositoriesShouldResideOnlyInPersistence() {
        JavaClasses imported = new ClassFileImporter()
                .importPackages("sumdu.edu.ua");

        classes()
                .that().haveSimpleNameEndingWith("Repository")
                .should().resideInAPackage("..persistence..")
                .check(imported);
    }
}
