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
		tem.setText("股金帐号     "+"["+sa.accountID+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		tem = new JLabel();
		tem.setText("分账号        "+"["+sa.accountSubID+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		
		tem = new JLabel();
		tem.setText("产品种类        "+"["+sa.productType+"]");
		infos.add(tem);
		subPanel.add(tem);

		tem = new JLabel();
		tem.setText("产品名称        "+"["+sa.productName+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		tem = new JLabel();
		tem.setText("账户名称        "+"["+sa.accountName+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		tem = new JLabel();
		tem.setText("存在状态        "+"["+sa.currentState+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		tem = new JLabel();
		tem.setText("账户余额        "+"["+sa.balance+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		tem = new JLabel();
		tem.setText("冻结/止付状态        "+"["+sa.freezeState+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		tem = new JLabel();
		tem.setText("冻结/止付金额        "+"["+sa.freezeAmount+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		tem = new JLabel();
		tem.setText("支取方式        "+"["+sa.drawType+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		tem = new JLabel();
		tem.setText("挂式状态        "+"["+sa.lossState+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		tem = new JLabel();
		tem.setText("开户日期        "+"["+sa.openDate+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		tem = new JLabel();
		tem.setText("凭证号码        "+"["+sa.dividendsAccount+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		tem = new JLabel();
		tem.setText("分红账户        "+"["+sa.voucherNumber+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		tem = new JLabel();
		tem.setText("销户日期        "+"["+sa.closeDate+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		tem = new JLabel();
		tem.setText("证件类型        "+"["+sa.IDType+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		tem = new JLabel();
		tem.setText("证件号码        "+"["+sa.ID+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		tem = new JLabel();
		tem.setText("开户网点        "+"["+sa.openSite+"]");
		infos.add(tem);
		subPanel.add(tem);
		
		tem = new JLabel();
		tem.setText("打印名称        "+"[]");
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
