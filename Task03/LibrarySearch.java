import java.awt.*;
import javax.swing.*;
public class LibrarySearch extends JFrame {
    public LibrarySearch(){
        super("Campus Library Search"); setLayout(new BorderLayout(8,8));
        JLabel title=new JLabel("Library Catalogue",SwingConstants.CENTER);
        JPanel search=new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField query=new JTextField(22); JComboBox<String> category=new JComboBox<>(new String[]{"All","Books","Journals","Authors"});
        JButton find=new JButton("Search"); search.add(query);search.add(category);search.add(find);
        JTextArea results=new JTextArea("Search results appear here."); results.setEditable(false);
        JScrollPane centre=new JScrollPane(results);
        JPanel controls=new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton prev=new JButton("Prev"),next=new JButton("Next"),reserve=new JButton("Reserve");
        controls.add(prev);controls.add(next);controls.add(reserve);
        JLabel status=new JLabel("Ready.");
        add(title,BorderLayout.NORTH); add(search,BorderLayout.PAGE_START); add(centre,BorderLayout.CENTER);
        JPanel south=new JPanel(new BorderLayout()); south.add(controls,BorderLayout.CENTER);south.add(status,BorderLayout.SOUTH);add(south,BorderLayout.SOUTH);
        find.addActionListener(e->{results.setText("Searching for: "+query.getText()+"\\nCategory: "+category.getSelectedItem());status.setText("Search complete (demo).");});
        setSize(650,420);setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    public static void main(String[] a){SwingUtilities.invokeLater(()->new LibrarySearch().setVisible(true));}
}
