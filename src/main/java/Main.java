public class Main {
    public static void main(String[] args) {
        // All good scenarios
        Person p1 = new Person();
        p1.setPersonID("56@#abcdAB");
        p1.setFirstName("Jane");
        p1.setLastName("Doe");
        p1.setAddress("101|Bourke Street|Melbourne|Victoria|Australia");
        p1.setBirthdate("15-11-1990");
        if(p1.addPerson()){
            System.out.println("Person added");
        } else {
            System.out.println("Person not added");
        }
    }
}