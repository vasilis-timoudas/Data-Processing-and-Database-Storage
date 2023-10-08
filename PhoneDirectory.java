// Διάβασμα από αρχείο δεδομένα(όνομα-τηλέφωνο) και επεξεργασία δεδομένων ανάλογα την επιλογή του χρήστη από το μενού και εισαγωγή σε τηλεφώνων σε βάση
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PhoneDirectory {
    
    public static void main(String[] args) {
       try {
            // Διάβασμα αρχείου από command line arguments
            BufferedReader in = new BufferedReader(new FileReader(args[0]));
            // Για διάβασμα ανά γραμμή του αρχείου
            String line = new String();
            // Πίνακας String που θα αποθηκευτούν τα στοιχεία του αρχείου
            String[][] DirEntries = new String[10][2];
            // Για να αποθηκεύετε το String μετά το split
            String[] enter = new String[2];
            // Για να αποθηκεύω όλα τα στοιχεία του αρχείου
            ArrayList<String> enterList = new ArrayList<String>();
            // path αρχείου
            String path;
            // Μετρητής
            int counter = 0;
            // Επιλογή
            int choice;   
            // Μέγεθος
            int size;
            // Για είσοδο 
            Scanner scanner = new Scanner(System.in);
	    // Για file path args
	    String argsPath = args[0];
	  
            
            // Μέχρι να τελειώσει το αρχείο
            while((line = in.readLine()) !=  null) {
                // split την γραμμή όπου υπάρχει ; 2 φορές
                enter = line.split(";", 2);
                
                // Προσθήκη στο ArrayList
                enterList.add(enter[0]);
                enterList.add(enter[1]);
            }
            // Αλλάζω το μέγεθος του πίνακα
            DirEntries = new String[enterList.size()/2][2];
            
            // Από το ArrayList περνάω στον 2D πίνακα τα στοιχεία
            for(int i=0; i<DirEntries.length; i++) {
                for(int j=0; j<2; j++) {
                    if(counter == enterList.size()) {
                        break;
                    }
                    
                    DirEntries[i][j] = enterList.get(counter);
                    
                    counter++;
                }
            }
            
            // Κλείσιμο αρχείου
            in.close();

	    // Εισαγωγή τηλεφωνικού καταλόγου στην βάση δεδομένων
	    /*
	    Το έχω σε σχόλια γιατί δεν έχω πρόβλημα με τον driver η λειτουργία του όμως είναι η παρακάτω
	    for(int i=0; i<DirEntries.length; i++) {
		insert(DirEntries[i][0], DirEntries[i][1], args[1]); 
	    }
             */
            
            // Μενού
            do {   
                // Επιλογή χρήστη
                choice = menu();
                
                // Εκτύπωση όλων των εγγραφών (ονόματος:τηλεφώνου) του καταλόγου ταξινομημένων κατ’ αλφαβητική σειρά (ονόματος).
                if(choice == 1) {
                    // Κλήσεις συναρτήσεων για ταξινόμηση και εμφάνιση
                    sortData(DirEntries);
                    printData(DirEntries);
                }
                // Εκτύπωση εγγραφών που ταιριάζουν με μοτίβα (κανονικές εκφράσεις).
                else if(choice == 2) {
                    System.out.println("Δώσε ένα αρχικό ονόματος");
                    char start = scanner.next().charAt(0);
                    
                    // Κλήση συνάρτησης για εμφάνιση ονόματωναπό αρχικό
                    findNameFromInitial(DirEntries, start);
                }
                // Εκτύπωση εγγραφής από το όνομα (εύρεση τηλεφώνου). 
                else if(choice == 3) {
                    System.out.println("Δώστε όνομα για εύρεση τηλεφώνου: ");
                    String selectName = scanner.nextLine();
                
                    // Κλήση συνάρτησης για εύρεση αριθμού
                    System.out.println(findPhoneFromName(DirEntries, selectName));
                }
                // Εισαγωγή νέας εγγραφής.
                else if(choice == 4) {
                    System.out.println("Δώστε όνομα και τηλέφωνο μαζί: ");
                    String selectNameAndPhone = scanner.nextLine();
                    
                    // Για προσθήκη ακόμα μίας εγγραφής
                    size = DirEntries.length + 1;
                    DirEntries = new String[size][2];
                    
                    // Άνοιγμα αρχείου για append
                    BufferedWriter out = new BufferedWriter(new FileWriter(args[0], true));
                    
                    // Γράφω στο αρχείο (append και όχι write για να μην διαγραφούν τα υπάρχοντα του αρχείου!)
                    out.write(selectNameAndPhone);
                    
                    // Κλείσιμο αρχείου
                    out.close();
                }
                // Τροποποίηση υπάρχουσας εγγραφής (αλλαγή του τηλεφώνου μόνο).
                else if(choice == 5) {
                    System.out.println("Δώστε το τηλέφωνο που θέλετε να αλλάξετε: ");
                    String oldPhone = scanner.nextLine();
                    System.out.println("Δώστε τον καινούργιο αριθμό: ");
                    String newPhone = scanner.nextLine();
                    
                    // Κλήση συνάρτησης για αλλαγή τηλεφώνου
                    changePhone(DirEntries, oldPhone, newPhone);
                    
                    // Κλήση συνάρτησης για αλλαγή και στο αρχείο
                    updateFile(DirEntries, argsPath);
                }
                // Διαγραφή εγγραφής.
                else if(choice == 6) {
                     System.out.println("Δώστε το τηλέφωνο για διαγραφή εγγραφής: ");
                     String deletedPhone = scanner.nextLine();
                     
                     // Κλήση συνάρτησης για διαγραφή εγγραφής
                     deleteDataFromPhone(DirEntries, deletedPhone);
                     
                     // Κλήση συνάρτησης για αλλαγή και στο αρχείο
                     updateFile(DirEntries, argsPath);
                }
                // Import: Εισαγωγή καταλόγου από αρχείο (csv).
                else if(choice == 7) {
                    // Μέχρι ο χρήστης να δώσει .csv αρχείο
                    do {     
                        System.out.println("Δώστε το path του αρχείου: ");
                        path = scanner.nextLine();
                       
                        
                    } while (!getFileExtension(path).equals("csv"));
                     
                     
                     // Κλήση συνάρτησης για εισαγωγή αρχείου csv
                      insertFile(DirEntries, path);
                }
		// Export: Ενημέρωση/αποθήκευση καταλόγου σε αρχείο (csv).
		else if(choice == 8) {
		      // Κλήση συνάρτησης για αλλαγή και στο αρχείο
                     updateFile(DirEntries, argsPath);
		}
               
           } while (choice != 0);  // μέχρι ο χρήστης να δώσει 0 για έξοδο
            

        } catch (IOException ioe) {
            System.out.println("Error: " + ioe);
        } 
       
    }
    
    // Συνάρτηση για το μενού
    private static int menu() {
        int choice = 1;
        
        do {      
            System.out.println("--------------------------------");
            System.out.println("1. Εκτύπωση όλων των εγγραφών (ονόματος:τηλεφώνου) του καταλόγου ταξινομημένων κατ’ αλφαβητική σειρά (ονόματος).");
            System.out.println("2. Εκτύπωση εγγραφών που ταιριάζουν με μοτίβα (κανονικές εκφράσεις).");
            System.out.println("3. Εκτύπωση εγγραφής από το όνομα (εύρεση τηλεφώνου).");
            System.out.println("4. Εισαγωγή νέας εγγραφής.");
            System.out.println("5. Τροποποίηση υπάρχουσας εγγραφής (αλλαγή του τηλεφώνου μόνο).");
            System.out.println("6. Διαγραφή εγγραφής.");
            System.out.println("7. Import: Εισαγωγή καταλόγου από αρχείο (csv).");
            System.out.println("8. Export: Ενημέρωση/αποθήκευση καταλόγου σε αρχείο (csv).");
            System.out.println("0. Έξοδος.");
            System.out.println("--------------------------------\n");
            
            Scanner scanner = new Scanner(System.in);
            System.out.println("Δώστε την επιλογή σας: ");
            
            choice = scanner.nextInt();
            
        } while (choice <= -1 || choice >= 8); // μέχρι ο χρήστης να δώσει σωστή επιλογή
        
        return choice;
    }
    
    // Εμφάνιση στοιχείων
    private static void printData(String[][] data) {
        for (int i=0; i < data.length; i++) {
                System.out.println(data[i][0] + " : " + data[i][1]);
        }
    }
    
    // Ταξινόμιση στοιχείων 
    private static void sortData(String[][] data) {
      for(int i=0; i<data.length - 1; i++)
          for(int j=i+1; j<data.length; j++)
              if(data[i][0].compareTo(data[j][0]) > 0) {
                  String temp = data[i][0];
                  data[i][0] = data[j][0];
                  data[j][0] = temp;
              }
    }
    
    // Εμφάνιση ονομάτων από αρχικό 
    private static void findNameFromInitial(String data[][], char initial) {
        for(int i=0; i<data.length; i++) {
            if(data[i][0].charAt(0) == initial) {
                System.out.println(data[i][0]);
            }
        }
    }
    
    // Εκτύπωση εγγραφής από το όνομα (εύρεση τηλεφώνου)
    private static String findPhoneFromName(String data[][], String name) {
        for(int i=0; i<data.length; i++) {
            if(name.equals(data[i][0])) {
                return "Βρέθηκε!\t" + data[i][1];
            }
        }
        
        return "Δεν βρέθηκε αυτό το όνομα!\n";
    }
    
    // Τροποποίηση υπάρχουσας εγγραφής (αλλαγή του τηλεφώνου μόνο).
    private static void changePhone(String data[][], String oldPhone, String newPhone) {
        boolean changed = false;
        
        for(int i=0; i<data.length; i++) {
            if(oldPhone.equals(data[i][1])) {
                System.out.println("Βρέθηκε αυτός αριθμός!");
                data[i][1] = newPhone;
                
                System.out.println("Αλλαγή αριθμού!");
                changed = true;
            }
        }
        
        if(!changed) {
            System.out.println("Δεν βρέθηκε αυτός ο αριθμός!");
        }
    }
     // Διαγραφή εγγραφής.
    private static void deleteDataFromPhone(String data[][], String deletedPhone) {
        boolean deleted = false;
        
        for(int i=0; i<data.length; i++) {
            if(deletedPhone.equals(data[i][1])) {
                 System.out.println("Βρέθηκε αυτός αριθμός!");
                 
                 // Διαγραφή αριθμού
                 data[i][0] = "";
                 data[i][1] = "";
                 
                 System.out.println("Διαγραφή εγγραφής!");
                 deleted = true;
            }
        }
        
        if(!deleted) {
            System.out.println("Δεν βρέθηκε αυτός ο αριθμός!");
        }
    }
    
    // Εισαγωγή αρχείου
    private static void insertFile(String data[][], String path) {
        try {
            // Διάβασμα αρχείου 
            BufferedReader in = new BufferedReader(new FileReader(path));
            // Για διάβασμα ανά γραμμή του αρχείου
            String line = new String();
            // Για να αποθηκεύετε το String μετά το split
            String[] enter = new String[2];
            // Για να αποθηκεύω όλα τα στοιχεία του αρχείου
            ArrayList<String> enterList = new ArrayList<String>();
            // Μετρητής
            int counter = 0;
            
            // Μέχρι να τελειώσει το αρχείο
            while((line = in.readLine()) != null) {
                // split την γραμμή όπου υπάρχει ; 2 φορές
                enter = line.split(";", 2);
                
                // Προσθήκη στο ArrayList
                enterList.add(enter[0]);
                enterList.add(enter[1]);
            }
            
             // Αλλάζω το μέγεθος του πίνακα
             data = new String[enterList.size()/2][2];
            
            // Από το ArrayList περνάω στον 2D πίνακα τα στοιχεία
            for(int i=0; i<data.length; i++) {
                for(int j=0; j<2; j++) {
                    if(counter == enterList.size()) {
                        break;
                    }
                    
                    data[i][j] = enterList.get(counter);
                    
                    counter++;
                }
            }
            
            // Κλείσιμο αρχείου
            in.close();
            
        } catch (IOException ioe) {
            System.out.println("Error: " + ioe);
        }
    }
    
    // Θα χρησιμοποιήσω αυτήν την συνάρτηση για να κάνω αλλαγές και στο αρχείο εκτός από τον 2D String πίνακα
    // π.χ. για διαγραφή ή τροποποίηση στοιχείου
    private static void updateFile(String data[][], String path) {
        try {
            // Διάβασμα αρχείου 
            BufferedWriter out = new BufferedWriter(new FileWriter(path));
            
            // Γράψιμο στο αρχείο
            for(int i=0; i<data.length; i++) {
                out.write(data[i][0] + ";" + data[i][1] + "\n");
            }
            
            // Κλείσιμο αρχείο
            out.close();
            
        } catch (IOException ioe) {
            System.out.println("Error: " + ioe);
        }
    } 
    
    // Συνάρτηση για να βρω την κατάληξη του αρχείου 
    // π.χ. αν τελειώνει σε .csv 
    private static String getFileExtension(String path) {
        if(path.lastIndexOf(".") != -1 && path.lastIndexOf(".") != 0)
            return path.substring(path.lastIndexOf(".") + 1);
        
        return "";
    }

    // Συνάρτηση για να γίνει σύνδεση στην βάση δεδομένων
    private static Connection connect(String dbFile) {
        // SQLite connection string
	String url = "jdbc:sqlite:C://sqlite/db/" + dbFile;		
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    // Συνάρτηση για να γίνει insert (name, phone)  στην βάση δεδομένων
    public static void insert(String name, String phone, String dbFile) {
        String sql = "INSERT INTO Persons(name, phone) VALUES(?,?)";

        try (Connection conn = connect(dbFile);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
