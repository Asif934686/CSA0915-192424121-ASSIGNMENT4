import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
public class AppointmentDesk extends JFrame {
    private final JTextField patientId=new JTextField(12);
    private final JComboBox<String> department=new JComboBox<>(new String[]{"Cardiology","Orthopaedics","Dermatology"});
    private final JComboBox<String> doctor=new JComboBox<>();
    private final JComboBox<String> slot=new JComboBox<>(new String[]{"09:00","10:00","11:00","14:00"});
    private final JLabel status=new JLabel("Ready.");
    private boolean bookingInProgress=false;
    private final Map<String,java.util.List<String>> doctors=Map.of(
      "Cardiology",java.util.List.of("Dr Rao","Dr Meena"),
      "Orthopaedics",java.util.List.of("Dr Kumar","Dr Fatima"),
      "Dermatology",java.util.List.of("Dr Shah","Dr Priya"));
    public AppointmentDesk(){
        super("Outpatient Appointment Desk"); setLayout(new BorderLayout(8,8));
        JPanel form=new JPanel(new GridLayout(4,2,6,6));
        form.add(new JLabel("Patient ID")); form.add(patientId);
        form.add(new JLabel("Department")); form.add(department);
        form.add(new JLabel("Doctor")); form.add(doctor);
        form.add(new JLabel("Time slot")); form.add(slot);
        JButton book=new JButton("Book");
        JPanel bottom=new JPanel(new BorderLayout()); bottom.add(book,BorderLayout.WEST); bottom.add(status,BorderLayout.CENTER);
        add(form,BorderLayout.CENTER); add(bottom,BorderLayout.SOUTH);
        department.addActionListener(e -> refreshDoctors());
        book.addActionListener(e -> book());
        addWindowListener(new WindowAdapter(){ @Override public void windowClosing(WindowEvent e){
            if(bookingInProgress && JOptionPane.showConfirmDialog(AppointmentDesk.this,"Booking is in progress. Close?","Confirm",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;
            dispose();
        }});
        refreshDoctors(); setSize(430,240); setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }
    private void refreshDoctors(){ doctor.removeAllItems(); for(String d:doctors.get((String)department.getSelectedItem()))doctor.addItem(d); }
    private void book(){
        if(patientId.getText().trim().isEmpty()){status.setText("Patient ID is required.");return;}
        bookingInProgress=true;
        try { status.setText("Appointment booked for "+patientId.getText()+" with "+doctor.getSelectedItem()+" at "+slot.getSelectedItem()); }
        finally { bookingInProgress=false; }
    }
    public static void main(String[] a){ SwingUtilities.invokeLater(()->new AppointmentDesk().setVisible(true)); }
}
