import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.concurrent.ExecutionException;
public class EventRegistrationApp extends JFrame {
    private final JTextField studentId=new JTextField(10), studentName=new JTextField(14);
    private final JComboBox<EventItem> eventBox=new JComboBox<>();
    private final JComboBox<EventItem> filterBox=new JComboBox<>();
    private final DefaultTableModel model=new DefaultTableModel(new String[]{"ID","Student ID","Name","Event","Registered At"},0);
    private final JTable table=new JTable(model);
    private final JLabel status=new JLabel("Ready");
    private final String url=System.getenv().getOrDefault("DB_URL","jdbc:mysql://localhost:3306/campus_events");
    private final String user=System.getenv().getOrDefault("DB_USER","root");
    private final String password=System.getenv().getOrDefault("DB_PASSWORD","");
    static class EventItem {int id;String name;EventItem(int i,String n){id=i;name=n;}public String toString(){return name;}}
    public EventRegistrationApp(){
        super("Campus Event Registration");setLayout(new BorderLayout(8,8));
        JMenuBar mb=new JMenuBar();JMenu file=new JMenu("File");JMenuItem refresh=new JMenuItem("Refresh"),exit=new JMenuItem("Exit");file.add(refresh);file.add(exit);mb.add(file);setJMenuBar(mb);
        JPanel form=new JPanel(new FlowLayout(FlowLayout.LEFT));form.add(new JLabel("Student ID"));form.add(studentId);form.add(new JLabel("Name"));form.add(studentName);form.add(eventBox);
        JButton register=new JButton("Register");form.add(register);
        JPanel filter=new JPanel(new FlowLayout(FlowLayout.LEFT));filter.add(new JLabel("Filter event"));filter.add(filterBox);JButton apply=new JButton("Apply");filter.add(apply);JButton export=new JButton("Export CSV");filter.add(export);
        JPanel north=new JPanel(new GridLayout(2,1));north.add(form);north.add(filter);add(north,BorderLayout.NORTH);add(new JScrollPane(table),BorderLayout.CENTER);add(status,BorderLayout.SOUTH);
        register.addActionListener(e->register());apply.addActionListener(e->loadPage());refresh.addActionListener(e->loadEvents());exit.addActionListener(e->dispose());export.addActionListener(e->exportCsv());
        setDefaultCloseOperation(EXIT_ON_CLOSE);setSize(850,500);loadEvents();
    }
    private Connection connect() throws SQLException{return DriverManager.getConnection(url,user,password);}
    private void loadEvents(){
        status.setText("Loading events...");
        new SwingWorker<java.util.List<EventItem>,Void>(){
            protected java.util.List<EventItem> doInBackground() throws Exception{
                java.util.List<EventItem> list=new java.util.ArrayList<>();
                try(Connection c=connect();Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT event_id,event_name FROM events ORDER BY event_name")){while(r.next())list.add(new EventItem(r.getInt(1),r.getString(2)));}
                return list;
            }
            protected void done(){try{eventBox.removeAllItems();filterBox.removeAllItems();for(EventItem x:get()){eventBox.addItem(x);filterBox.addItem(x);}loadPage();status.setText("Events loaded.");}catch(Exception ex){status.setText("Load failed: "+ex.getMessage());}}
        }.execute();
    }
    private void register(){
        String sid=studentId.getText().trim(),name=studentName.getText().trim();EventItem ev=(EventItem)eventBox.getSelectedItem();
        if(sid.isEmpty()||name.isEmpty()||ev==null){JOptionPane.showMessageDialog(this,"Enter student ID, name and select an event.");return;}
        status.setText("Registering...");
        new SwingWorker<Void,Void>(){
            protected Void doInBackground() throws Exception{
                String sql="INSERT INTO registrations(student_id,student_name,event_id) VALUES(?,?,?)";
                try(Connection c=connect();PreparedStatement p=c.prepareStatement(sql)){p.setString(1,sid);p.setString(2,name);p.setInt(3,ev.id);p.executeUpdate();}return null;
            }
            protected void done(){try{get();status.setText("Registration successful.");loadPage();}catch(ExecutionException ex){Throwable cause=ex.getCause();if(cause instanceof SQLException && ((SQLException)cause).getErrorCode()==1062)status.setText("Duplicate: student already registered for this event.");else status.setText("Registration failed: "+cause.getMessage());}catch(Exception ex){status.setText(ex.getMessage());}}
        }.execute();
    }
    private void loadPage(){
        EventItem ev=(EventItem)filterBox.getSelectedItem();status.setText("Loading registrations...");
        new SwingWorker<java.util.List<Object[]>,Void>(){
            protected java.util.List<Object[]> doInBackground() throws Exception{
                java.util.List<Object[]> rows=new java.util.ArrayList<>();
                String sql="SELECT r.registration_id,r.student_id,r.student_name,e.event_name,r.registered_at FROM registrations r JOIN events e ON e.event_id=r.event_id "+(ev==null?"":"WHERE e.event_id=? ")+"ORDER BY r.registration_id DESC LIMIT 100 OFFSET 0";
                try(Connection c=connect();PreparedStatement p=c.prepareStatement(sql)){if(ev!=null)p.setInt(1,ev.id);try(ResultSet rs=p.executeQuery()){while(rs.next())rows.add(new Object[]{rs.getLong(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getTimestamp(5)});}}
                return rows;
            }
            protected void done(){try{model.setRowCount(0);for(Object[] row:get())model.addRow(row);status.setText("Page loaded.");}catch(Exception ex){status.setText("Query failed: "+ex.getMessage());}}
        }.execute();
    }
    private void exportCsv(){
        JFileChooser chooser=new JFileChooser();if(chooser.showSaveDialog(this)!=JFileChooser.APPROVE_OPTION)return;
        try(java.io.PrintWriter out=new java.io.PrintWriter(chooser.getSelectedFile())){
            for(int c=0;c<model.getColumnCount();c++){if(c>0)out.print(",");out.print(model.getColumnName(c));}out.println();
            for(int r=0;r<model.getRowCount();r++){for(int c=0;c<model.getColumnCount();c++){if(c>0)out.print(",");String v=String.valueOf(model.getValueAt(r,c)).replace("\"","\"\"");out.print("\""+v+"\"");}out.println();}
            status.setText("CSV exported.");
        }catch(Exception ex){status.setText("Export failed: "+ex.getMessage());}
    }
    public static void main(String[] a){SwingUtilities.invokeLater(()->new EventRegistrationApp().setVisible(true));}
}
