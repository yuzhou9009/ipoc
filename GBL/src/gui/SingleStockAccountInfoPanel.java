package gui;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class SingleStockAccountInfoPanel extends JPanel{

	List<JLabel> infos = new ArrayList<JLabel>();
	JScrollPane ss_jsp;
	JPanel subPanel = new JPanel();
	
	public SingleStockAccountInfoPanel(StockAccount sa)
	{
		subPanel.setLayout(new GridLayout(19,1));
		
		JLabel tem;
		
		tem = new JLabel();
		tem.setText("�ɽ��ʺ�     "+"["+sa.accountID+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		tem = new JLabel();
		tem.setText("���˺�        "+"["+sa.accountSubID+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		
		tem = new JLabel();
		tem.setText("��Ʒ����        "+"["+sa.productType+"]");
		infos.add(tem);
		subPanel.add(tem);

		tem = new JLabel();
		tem.setText("��Ʒ����        "+"["+sa.productName+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		tem = new JLabel();
		tem.setText("�˻�����        "+"["+sa.accountName+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		tem = new JLabel();
		tem.setText("����״̬        "+"["+sa.currentState+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		tem = new JLabel();
		tem.setText("�˻����        "+"["+sa.balance+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		tem = new JLabel();
		tem.setText("����/ֹ��״̬        "+"["+sa.freezeState+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		tem = new JLabel();
		tem.setText("����/ֹ�����        "+"["+sa.freezeAmount+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		tem = new JLabel();
		tem.setText("֧ȡ��ʽ        "+"["+sa.drawType+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		tem = new JLabel();
		tem.setText("��ʽ״̬        "+"["+sa.lossState+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		tem = new JLabel();
		tem.setText("��������        "+"["+sa.openDate+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		tem = new JLabel();
		tem.setText("ƾ֤����        "+"["+sa.dividendsAccount+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		tem = new JLabel();
		tem.setText("�ֺ��˻�        "+"["+sa.voucherNumber+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		tem = new JLabel();
		tem.setText("��������        "+"["+sa.closeDate+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		tem = new JLabel();
		tem.setText("֤������        "+"["+sa.IDType+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		tem = new JLabel();
		tem.setText("֤������        "+"["+sa.ID+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		tem = new JLabel();
		tem.setText("��������        "+"["+sa.openSite+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		tem = new JLabel();
		tem.setText("��ӡ����        "+"[]");
		infos.add(tem);
		subPanel.add(tem);

/*		public String accountID;
		public String accountSubID;
		public int productType;
		public String productName;
		public String accountName;
		public String currentState;
		public float balance;
		public String freezeState;
		public float freezeAmount;
		public String drawType;
		public String lossState;
		public Date openDate;
		public String dividendsAccount;
		public String voucherNumber;
		public Date closeDate;
		public String IDType;
		public String ID;
		public int openSite;*/
		
	}
	
}
