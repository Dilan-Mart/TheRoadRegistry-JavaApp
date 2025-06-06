import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Person {

    private String personID;
    private String firstName;
    private String lastName;
    private String address;
    private String birthdate;
    private HashMap<Date, Integer> demeritPoints = new HashMap<>();
    private boolean isSuspended;

    public Person(String personID, String firstName, String lastName, String address, String birthDate) {
        this.personID = personID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.birthdate = birthDate;
    }
    
    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    // File path for storing records
    private static final String FILE_PATH = "persons.txt";

    @Override
    public String toString() {
        String str = String.join(", ", this.personID, this.firstName, this.lastName, this.address, this.birthdate) + "||";
        if (!this.demeritPoints.isEmpty()) {

            str += this.demeritPoints.toString();
        }
        return str;
    }


    public boolean addPerson() {
        if (isInvalidPersonID(this.personID) || isInvalidAddress(this.address) || isInvalidBirthdate(this.birthdate)) {
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(this.toString());
            writer.newLine();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean updatePersonalDetails(String newID, String newFirstName, String newLastName, String newAddress, String newBirthdate) {
        // Validate new values first
        if (isInvalidPersonID(newID) || isInvalidAddress(newAddress) || isInvalidBirthdate(newBirthdate))
            return false;

        boolean isDOBChanged = !this.birthdate.equals(newBirthdate);
        boolean isAddressChanged = !this.address.equals(newAddress);
        boolean isIDChanged = !this.personID.equals(newID);

        // Update file
        List<String> lines = new ArrayList<>();
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split("\\|\\|");
                String pData = split[0];
                String pDemerit = "";
                if(split.length == 2) {
                    pDemerit = split[1];
                }
                String[] lineArr = pData.split("\\s*,\\s*");

                // If under 18, address cannot change
                if (getAge(this.birthdate) < 18 && isAddressChanged)
                    return false;

                // If birthdate changes, no other detail can change
                if (isDOBChanged && (isIDChanged || !firstName.equals(newFirstName) || !lastName.equals(newLastName) || isAddressChanged))
                    return false;
                else {
                    this.setBirthdate(newBirthdate);
                }

                // If first char of personID is even, personID cannot change
                char ch = this.personID.charAt(0);
                if (Character.isDigit(ch) && ((int) ch) % 2 == 0 && isIDChanged)
                    return false;

                if (lineArr[0].equals(this.personID)) {
                    line = String.join(", ", newID, newFirstName, newLastName, newAddress, newBirthdate);
                    if(!pDemerit.isEmpty()){
                        line += "||" + pDemerit;
                    }
                    updated = true;
                }
                lines.add(line);
            }
        } catch (IOException e) {
            return false;
        }

        if (updated) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
                for (String s : lines) {
                    writer.write(s);
                    writer.newLine();
                }
                // Update instance fields
                this.personID = newID;
                this.firstName = newFirstName;
                this.lastName = newLastName;
                this.address = newAddress;
                this.birthdate = newBirthdate;
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        return false;
    }

    public String addDemeritPoints(String offenseDate, int points) {
        if (!offenseDate.matches("\\d{2}-\\d{2}-\\d{4}") || points < 1 || points > 6)
            return "Failed";

        List<String> lines = new ArrayList<>();
        boolean updated = false;

        try {
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(offenseDate);
            this.demeritPoints.put(date, points);

            try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] split = line.split("\\|\\|");
                    String pData = split[0];

                    String[] lineArr = pData.split("\\s*,\\s*");

                    if (lineArr[0].equals(this.personID)) {
                        line = pData + "||" + this.demeritPoints.toString();
                        updated = true;
                    }
                    lines.add(line);
                }
            } catch (IOException e) {
                return "Failed";
            }

            if (updated) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
                    for (String s : lines) {
                        writer.write(s);
                        writer.newLine();
                    }

                } catch (IOException e) {
                    return "Failed";
                }
            }

            int age = getAge(this.birthdate);
            int totalPoints = getPointsInLast2Years();

            if ((age < 21 && totalPoints > 6) || (age >= 21 && totalPoints > 12)) {
                this.isSuspended = true;
            }

            return "Success";
        } catch (ParseException e) {
            return "Failed";
        }
    }
    
    public boolean isSuspended() {
        return this.isSuspended;
    }

    private boolean isInvalidPersonID(String id) {
        if (id == null || id.length() != 10)
            return true;

        if (!id.substring(0, 2).matches("[2-9]{2}"))
            return true;

        String mid_str = id.substring(2, 8);
        long specialCount = mid_str.chars().filter(c -> !Character.isLetterOrDigit(c)).count();
        if (specialCount < 2)
            return true;

        return !id.substring(8).matches("[A-Z]{2}");
    }

    private boolean isInvalidAddress(String addr) {
        String[] addPart = addr.split("\\|");
        return addPart.length != 5 || !addPart[3].equals("Victoria");
    }

    private boolean isInvalidBirthdate(String date) {
        return !date.matches("\\d{2}-\\d{2}-\\d{4}");
    }

    private int getAge(String dateStr) {
        try {
            LocalDate bDay = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            return Period.between(bDay, LocalDate.now()).getYears();
        } catch (Exception e) {
            return -1;
        }
    }

    private int getPointsInLast2Years() {
        Calendar cutoff = Calendar.getInstance();
        cutoff.add(Calendar.YEAR, -2);
        Date threshold = cutoff.getTime();

        return demeritPoints.entrySet().stream()
                .filter(entry -> entry.getKey().after(threshold))
                .mapToInt(Map.Entry::getValue)
                .sum();
    }

}
