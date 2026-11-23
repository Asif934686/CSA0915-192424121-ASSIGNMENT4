import java.awt.FlowLayout;
import javax.swing.*;
public class LambdaConverter extends JFrame {
    private final JTextField amount=new JTextField(10);
    private final JComboBox<String> target=new JComboBox<>(new String[]{"USD","EUR"});
    private final JLabel result=new JLabel(" ");
    private static double rate(String c){return c.equals("USD")?0.012:0.011;}
    public LambdaConverter(){
        super("Currency Converter");setLayout(new FlowLayout());
        JButton convert=new JButton("Convert");add(new JLabel("INR"));add(amount);add(target);add(convert);add(result);
        // One shared lambda instance registered with both triggers.
        java.awt.event.ActionListener handler=e->convert();
        convert.addActionListener(handler);amount.addActionListener(handler);
        setDefaultCloseOperation(EXIT_ON_CLOSE);pack();
    }
    private void convert(){
        try {double value=Double.parseDouble(amount.getText().trim());if(!Double.isFinite(value)||value<0)throw new NumberFormatException();
            String out=String.format("%.2f %s",value*rate((String)target.getSelectedItem()),target.getSelectedItem());
            result.setText(out);JOptionPane.showMessageDialog(this,out,"Conversion",JOptionPane.INFORMATION_MESSAGE);
        } catch(NumberFormatException ex){result.setText("Invalid amount");JOptionPane.showMessageDialog(this,"Invalid amount","Error",JOptionPane.ERROR_MESSAGE);}
    }
    public static void main(String[] a){SwingUtilities.invokeLater(()->new LambdaConverter().setVisible(true));}
}
