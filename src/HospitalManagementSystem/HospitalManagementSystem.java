package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {    // it is main driver class
    private static final String url = "jdbc:mysql://localhost:3306/hospital_management";
    private static final String username ="root";
    private static final String password = "";

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);
        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            Patients patients = new Patients(connection, scanner);
            Doctor doctor = new Doctor(connection);

            while(true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM ");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.println("Enter your choice: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        // Add Patient
                        patients.addPatient();
                        System.out.println();
                        break;
                    case 2:
                        // View Patient
                        patients.viewPatients();
                        System.out.println();
                        break;
                    case 3:
                        // View Doctors
                        doctor.viewDoctors();
                        System.out.println();
                        break;
                    case 4:
                        // Book Appointments
                        bookAppointment(patients, doctor, connection, scanner);
                        System.out.println();
                        break;
                    case 5:
                        System.out.println("Exited from HOSPITAL MANAGEMENT SYSTEM");
                        return;
                    default:
                        System.out.println("Enter valid choice..");
                        break;
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    // patient should be added already in patients record for appointment.
    public static void bookAppointment(Patients patients, Doctor doctor, Connection connection, Scanner scanner){
        System.out.println("Enter Patient Id: ");
        int patientId = scanner.nextInt();
        System.out.println("Enter Doctor Id: ");
        int doctorId = scanner.nextInt();
        System.out.println("Enter appointment date (YYYY-MM-DD: ");
        String appointmentDate = scanner.next();

        if(patients.checkPatient(patientId) && doctor.checkDoctor(doctorId)){
            if(checkDoctorAvailability(doctorId, appointmentDate,connection)){
                String appointmentQuery = "insert into appointments(patient_id, doctor_id, appointment_date) values(?, ?, ?)";
                try{
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appointmentDate);

                    int rowsAffected = preparedStatement.executeUpdate();
                    if(rowsAffected > 0){
                        System.out.println("Appointment Booked....");
                    }else{
                        System.out.println("Failed to book appointment....");
                    }
                }catch (SQLException e){
                    e.printStackTrace();
                }

            }else{
                System.out.println("Doctor not available on this date..");
            }
        }else{
            System.out.println("Either doctor or patient doesn't exist .....");
        }
    }

    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection){
       String query = "select count(*) from appointments where doctor_id =? and appointment_date =?";
       try{
           PreparedStatement preparedStatement = connection.prepareStatement(query);
           preparedStatement.setInt(1, doctorId);
           preparedStatement.setString(2, appointmentDate);
           ResultSet resultSet = preparedStatement.executeQuery();
           if(resultSet.next()){
               int count = resultSet.getInt(1);
               if(count == 0){
                   return true;
               }
           }
       }catch (SQLException e){
           e.printStackTrace();
       }
       return false;
    }
}
