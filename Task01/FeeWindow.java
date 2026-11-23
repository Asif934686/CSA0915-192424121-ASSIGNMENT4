import java.awt.*;
import java.awt.event.*;
public class FeeWindow extends Frame {
    private final TextField regNo=new TextField(18), amount=new TextField(18);
    private final Label status=new Label("Enter payment details.");
    private final FeeService service=new FeeService("jdbc:mysql://localhost:3306/college","root","YOUR_PASSWORD");
    public FeeWindow() {
        super("Student Fee Payment");
        setLayout(new BorderLayout(8,8));
        Panel fields=new Panel(new GridLayout(2,2,6,6));
        fields.add(new Label("Registration number")); fields.add(regNo);
        fields.add(new Label("Amount")); fields.add(amount);
        Panel actions=new Panel(new FlowLayout());
        Button pay=new Button("Pay"), clear=new Button("Clear"), receipt=new Button("Receipt");
        actions.add(pay); actions.add(clear); actions.add(receipt);
        add(fields,BorderLayout.CENTER); add(actions,BorderLayout.SOUTH); add(status,BorderLayout.NORTH);
        pay.addActionListener(e -> doPay());
        clear.addActionListener(e -> { regNo.setText(""); amount.setText(""); status.setText("Fields cleared."); });
        receipt.addActionListener(e -> status.setText("Receipt: " + lastReceipt));
        addWindowListener(new WindowAdapter(){ public void windowClosing(WindowEvent e){ dispose(); }});
        setSize(420,190);
    }
    private String lastReceipt="No payment recorded.";
    private void doPay() {
        try { lastReceipt=service.pay(regNo.getText(),amount.getText()); status.setText(lastReceipt); }
        catch(Exception ex){ status.setText("Payment failed: "+ex.getMessage()); }
    }
    public static void main(String[] args){ EventQueue.invokeLater(() -> new FeeWindow().setVisible(true)); }
}
