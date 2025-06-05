import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import java.io.File;

public class test {
	
	private static final String FILE_NAME = "persons.txt";

    @BeforeAll
    public static void setup() {
        // Delete file before each test to isolate test data

        File file = new File(FILE_NAME);
        if (file.exists()) file.delete();
    }

//    @AfterAll
//    public static void cleanup() {
//        // Clean up file after tests
//        File file = new File(FILE_NAME);
//    }

    // ---------------------------
    // Tests for addPerson
    // ---------------------------

    @Test
    public void testAddPerson_Valid() {
      System.out.println("Person added");
        Person p = new Person("56s_d%&fAB", "John", "Doe", "10|Main St|Melbourne|Victoria|Australia", "15-11-2000");
        assertTrue(p.addPerson());
    }

    @Test
    public void testAddPerson_InvalidID() {
        Person p = new Person("12abcdXY", "Jane", "Smith", "10|Main St|Melbourne|Victoria|Australia", "15-11-2000");
        assertFalse(p.addPerson());
    }

    @Test
    public void testAddPerson_InvalidAddress() {
        Person p = new Person("78s_d!f#AB", "Alice", "Brown", "15|Smith St|Sydney|NSW|Australia", "15-11-2000");
        assertFalse(p.addPerson());
    }

    @Test
    public void testAddPerson_InvalidBirthDate() {
        Person p = new Person("89&^d!x@AB", "Bob", "Lee", "22|Hill St|Melbourne|Victoria|Australia", "1990-11-15");
        assertFalse(p.addPerson());
    }

    @Test
    public void testAddPerson_InvalidSpecialCharacters() { 
        Person p = new Person("56abcdzAB", "Tom", "Hanks", "1|Star Rd|Melbourne|Victoria|Australia", "15-11-1990");
        assertFalse(p.addPerson());
    }

    // ---------------------------
    // Tests for updatePersonalDetails
    // ---------------------------

    @Test
    public void testUpdateDetails_AllValid() {
        Person p = new Person("56s_d%&fAB", "John", "Doe", "10|Main St|Melbourne|Victoria|Australia", "15-11-2000");
        p.addPerson();
        boolean result = p.updatePersonalDetails("56s_d%&fAB", "Johnny", "Doe", "11|Main St|Melbourne|Victoria|Australia", "15-11-2000");
        assertTrue(result);
    }

    @Test
    public void testUpdateDetails_Under18CannotChangeAddress() {
        Person p = new Person("56s_d%&fAB", "John", "Doe", "5|Main St|Melbourne|Victoria|Australia", "01-01-2010");
        p.addPerson();
        boolean result = p.updatePersonalDetails("56s_d%&fAB", "John", "Doe", "99|New St|Melbourne|Victoria|Australia", "01-01-2010");
        assertFalse(result);
    }

    @Test
    public void testUpdateDetails_BirthdayChangeOnly() {
        Person p = new Person("56s_d%&fAB", "Emma", "Stone", "10|Sunset Blvd|Melbourne|Victoria|Australia", "01-01-1995");
        p.addPerson();
        boolean result = p.updatePersonalDetails("56s_d%&fAB", "Emma", "Stone", "10|Sunset Blvd|Melbourne|Victoria|Australia", "01-01-1996");
        assertTrue(result);
    }

    @Test
    public void testUpdateDetails_BirthdayChangeWithOtherChanges() {
        Person p = new Person("56s_d%&fAB", "Leo", "DiCaprio", "11|Sunrise Rd|Melbourne|Victoria|Australia", "01-01-1990");
        p.addPerson();
        boolean result = p.updatePersonalDetails("99a_d%&zAB", "Leo", "DiCaprio", "22|Different Rd|Melbourne|Victoria|Australia", "01-01-1992");
        assertFalse(result);
    }

    @Test
    public void testUpdateDetails_IDChangeNotAllowedIfFirstCharEven() {
        Person p = new Person("62a_d!x@AB", "Eve", "Black", "5|St Kilda Rd|Melbourne|Victoria|Australia", "01-01-1990");
        p.addPerson();
        boolean result = p.updatePersonalDetails("72x_d!k@CD", "Eve", "Black", "5|St Kilda Rd|Melbourne|Victoria|Australia", "01-01-1990");
        assertFalse(result);
    }

    // ---------------------------
    // Tests for addDemeritPoints
    // ---------------------------

    @Test
    public void testAddDemeritPoints_ValidCase() {
        Person p = new Person("78a_x!z@AB", "Clark", "Kent", "2|Hero Ln|Melbourne|Victoria|Australia", "01-01-2000");
        p.addPerson();
        String result = p.addDemeritPoints("01-01-2023", 3);
        assertEquals("Success", result);
        assertFalse(p.isSuspended());
    }

    @Test
    public void testAddDemeritPoints_ExceedsThreshold_Under21() {
        Person p = new Person("79b_d!z@AB", "Peter", "Parker", "3|Web St|Melbourne|Victoria|Australia", "01-01-2007");
        p.addPerson();
        p.addDemeritPoints("01-01-2025", 4);
        p.addDemeritPoints("01-02-2025", 4);
        assertTrue(p.isSuspended());
    }

    @Test
    public void testAddDemeritPoints_ExceedsThreshold_Over21() {
        Person p = new Person("89x_c@!dAB", "Bruce", "Wayne", "100|Batcave Rd|Melbourne|Victoria|Australia", "01-01-1990");
        p.addPerson();
        for (int i = 0; i < 3; i++) {
            p.addDemeritPoints("01-01-2025", 5);
            p.addDemeritPoints("01-02-2025", 5);
            p.addDemeritPoints("01-03-2025", 5);
        }
        assertTrue(p.isSuspended());
    }

    @Test
    public void testAddDemeritPoints_InvalidDate() {
        Person p = new Person("93m_d!z@AB", "Tony", "Stark", "108|Arc Rd|Melbourne|Victoria|Australia", "01-01-1985");
        p.addPerson();
        String result = p.addDemeritPoints("2023/01/01", 3);
        assertEquals("Failed", result);
    }

    @Test
    public void testAddDemeritPoints_InvalidPoints() {
        Person p = new Person("95n_d!z@AB", "Natasha", "Romanoff", "9|Spy St|Melbourne|Victoria|Australia", "01-01-1992");
        p.addPerson();
        String result = p.addDemeritPoints("01-01-2023", 10);
        assertEquals("Failed", result);
    }
	
}
