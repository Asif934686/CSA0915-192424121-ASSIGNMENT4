import java.awt.*;import java.awt.event.*;import javax.swing.*;
public class FrameListenerConverter extends JFrame implements ActionListener{
 JTextField amount=new JTextField(8); JComboBox<String> target=new JComboBox<>(new String[]{"USD","EUR"}); JLabel result=new JLabel();
 public FrameListenerConverter(){setLayout(new FlowLayout());JButton b=new JButton("Convert");add(amount);add(target);add(b);add(result);b.addActionListener(this);amount.addActionListener(this);addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e){dispose();}});pack();}
 public void actionPerformed(ActionEvent e){try{double v=Double.parseDouble(amount.getText());String s=String.format("%.2f %s",v*(target.getSelectedItem().equals("USD")?.012:.011),target.getSelectedItem());result.setText(s);JOptionPane.showMessageDialog(this,s);}catch(Exception x){result.setText("Invalid amount");}}
 public static void main(String[]x){SwingUtilities.invokeLater(()->new FrameListenerConverter().setVisible(true));}
}
