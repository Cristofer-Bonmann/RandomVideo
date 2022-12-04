import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.randomvideo.RandomVideoTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        RandomVideoTest.class,
})
public class SuiteExec {

  @BeforeClass
  public static void setUp() {
    System.out.println("Testes iniciados.");
  }
}
