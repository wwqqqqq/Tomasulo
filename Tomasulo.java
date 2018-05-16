import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

//import com.sun.org.apache.xpath.internal.operations.String;
import java.lang.String;
/**
 * @author yanqing.qyq 2012-2015@USTC
 * 模板说明：该模板主要提供依赖Swing组件提供的JPanle，JFrame，JButton等提供的GUI。使用“监听器”模式监听各个Button的事件，从而根据具体事件执行不同方法。
 * Tomasulo算法核心需同学们自行完成，见说明（4）
 * 对于界面必须修改部分，见说明(1),(2),(3)
 *
 *  (1)说明：根据你的设计完善指令设置中的下拉框内容
 *	(2)说明：请根据你的设计指定各个面板（指令状态，保留站，Load部件，寄存器部件）的大小
 *	(3)说明：设置界面默认指令
 *	(4)说明： Tomasulo算法实现
 */
@SuppressWarnings("unchecked") 
public class Tomasulo extends JFrame implements ActionListener{
	/*
	 * 界面上有六个面板：
	 * ins_set_panel : 指令设置
	 * EX_time_set_panel : 执行时间设置
	 * ins_state_panel : 指令状态
	 * RS_panel : 保留站状态
	 * Load_panel : Load部件
	 * Registers_state_panel : 寄存器状态
	 */
	private JPanel ins_set_panel,EX_time_set_panel,ins_state_panel,RS_panel,Load_panel,Registers_state_panel;

	/*
	 * 四个操作按钮：步进，进5步，重置，执行
	 */
	private JButton stepbut,step5but,resetbut,startbut;

	/*
	 * 指令选择框
	 */
	private JComboBox inst_typebox[]=new JComboBox[24];

	/*
	 * 每个面板的名称
	 */
	private JLabel inst_typel, timel, tl1,tl2,tl3,tl4,resl,regl,ldl,insl,stepsl;
	private int time[]=new int[4];
	//time[0..3] - load，加减，乘法，除法

	/*
	 * 部件执行时间的输入框
	 */
	private JTextField tt1,tt2,tt3,tt4;

	private int intv[][]=new int[6][4],cnow,inst_typenow=0;
	private int cal[][]={{-1,0,0},{-1,0,0},{-1,0,0},{-1,0,0},{-1,0,0}};
	private int ld[][]={{0,0},{0,0},{0,0}};
	private int ff[]={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

	/*
	 * (1)说明：根据你的设计完善指令设置中的下拉框内容
	 * inst_type： 指令下拉框内容:"NOP","L.D","ADD.D","SUB.D","MULT.D","DIV.D"…………
	 * regist_table：       目的寄存器下拉框内容:"F0","F2","F4","F6","F8" …………
	 * rx：       源操作数寄存器内容:"R0","R1","R2","R3","R4","R5","R6","R7","R8","R9" …………
	 * ix：       立即数下拉框内容:"0","1","2","3","4","5","6","7","8","9" …………
	 */
	private String  inst_type[]={"NOP","L.D","ADD.D","SUB.D","MULT.D","DIV.D"},
					regist_table[]={"F0","F2","F4","F6","F8","F10","F12","F14","F16"
							,"F18","F20","F22","F24","F26","F28","F30","F32"},
					rx[]={"R0","R1","R2","R3","R4","R5","R6","R7","R8","R9","R10","R11","R12","R13","R14","R15","R16","R17","R18","R19","R20",
							"R21","R22","R23","R24","R25","R26","R27","R28","R29","R30","R31"},
					ix[]={"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24",
							"25","26","27","28","29","30","31"};

	/*
	 * (2)说明：请根据你的设计指定各个面板（指令状态，保留站，Load部件，寄存器部件）的大小
	 * 		指令状态 面板
	 * 		保留站 面板
	 * 		Load部件 面板
	 * 		寄存器 面板
	 * 					的大小
	 */
	private	String  my_inst_type[][]=new String[7][4], my_rs[][]=new String[6][8],
					my_load[][]=new String[4][4], my_regsters[][]=new String[3][17];
	private	JLabel  inst_typejl[][]=new JLabel[7][4], resjl[][]=new JLabel[6][8],
					ldjl[][]=new JLabel[4][4], regjl[][]=new JLabel[3][17];

//构造方法
	public Tomasulo(){
		super("Tomasulo Simulator");

		//设置布局
		Container cp=getContentPane();
		FlowLayout layout=new FlowLayout();
		cp.setLayout(layout);
		int fontsize = 24;

		//指令设置。GridLayout(int 指令条数, int 操作码+操作数, int hgap, int vgap)
		inst_typel = new JLabel("指令设置");
		ins_set_panel = new JPanel(new GridLayout(6,4,0,0));
		ins_set_panel.setPreferredSize(new Dimension(500, 200));
		ins_set_panel.setBorder(new EtchedBorder(EtchedBorder.RAISED));

		//操作按钮:执行，重设，步进，步进5步
		timel = new JLabel("执行时间设置");
		EX_time_set_panel = new JPanel(new GridLayout(2,4,0,0));
		EX_time_set_panel.setPreferredSize(new Dimension(280, 100));
		EX_time_set_panel.setBorder(new EtchedBorder(EtchedBorder.RAISED));

		//指令状态
		insl = new JLabel("指令状态");
		ins_state_panel = new JPanel(new GridLayout(7,4,0,0));
		ins_state_panel.setPreferredSize(new Dimension(1000, 250));
		ins_state_panel.setBorder(new EtchedBorder(EtchedBorder.RAISED));


		//寄存器状态
		regl = new JLabel("寄存器");
		Registers_state_panel = new JPanel(new GridLayout(3,17,0,0));
		Registers_state_panel.setPreferredSize(new Dimension(2000, 100));
		Registers_state_panel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		//保留站
		resl = new JLabel("保留站");
		RS_panel = new JPanel(new GridLayout(6,7,0,0));
		RS_panel.setPreferredSize(new Dimension(2000, 180));
		RS_panel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		//Load部件
		ldl = new JLabel("Load部件");
		Load_panel = new JPanel(new GridLayout(4,4,0,0));
		Load_panel.setPreferredSize(new Dimension(1000, 120));
		Load_panel.setBorder(new EtchedBorder(EtchedBorder.RAISED));

		tl1 = new JLabel("Load");
		tl2 = new JLabel("加/减");
		tl3 = new JLabel("乘法");
		tl4 = new JLabel("除法");

//操作按钮:执行，重设，步进，步进5步
		stepsl = new JLabel();
		stepsl.setPreferredSize(new Dimension(200, 30));
		stepsl.setHorizontalAlignment(SwingConstants.CENTER);
		stepsl.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		stepbut = new JButton("步进");
		stepbut.addActionListener(this);
		step5but = new JButton("步进5步");
		step5but.addActionListener(this);
		startbut = new JButton("执行");
		startbut.addActionListener(this);
		resetbut= new JButton("重设");
		resetbut.addActionListener(this);
		tt1 = new JTextField("2");   //load
		tt2 = new JTextField("2");   //加减
		tt3 = new JTextField("10");  //乘法
		tt4 = new JTextField("40");  //除法


//指令设置
		/*
		 * 设置指令选择框（操作码，操作数，立即数等）的default选择
		 */
		//默认选项中，前两个的源操作数是ix和rx，后四个的源操作数是f
		for (int i=0;i<2;i++)
			for (int j=0;j<4;j++){
				if (j==0){
					inst_typebox[i*4+j]=new JComboBox(inst_type);
				}
				else if (j==1){
					inst_typebox[i*4+j]=new JComboBox(regist_table);
				}
				else if (j==2){
					inst_typebox[i*4+j]=new JComboBox(ix);
				}
				else {
					inst_typebox[i*4+j]=new JComboBox(rx);
				}
				inst_typebox[i*4+j].addActionListener(this);
				inst_typebox[i*4+j].setFont(new java.awt.Font("Dialog",0,fontsize));
				ins_set_panel.add(inst_typebox[i*4+j]);
			}
		for (int i=2;i<6;i++)
			for (int j=0;j<4;j++){
				if (j==0){
					inst_typebox[i*4+j]=new JComboBox(inst_type);
				}
				else {
					inst_typebox[i*4+j]=new JComboBox(regist_table);
				}
				inst_typebox[i*4+j].addActionListener(this);
				inst_typebox[i*4+j].setFont(new java.awt.Font("Dialog",0,fontsize));
				ins_set_panel.add(inst_typebox[i*4+j]);
			}
		/*
		 * (3)说明：设置界面默认指令，根据你设计的指令，操作数等的选择范围进行设置。
		 * 默认6条指令。待修改
		 */
		inst_typebox[0].setSelectedIndex(1);
		inst_typebox[1].setSelectedIndex(3);
		inst_typebox[2].setSelectedIndex(21);
		inst_typebox[3].setSelectedIndex(2);

		inst_typebox[4].setSelectedIndex(1);
		inst_typebox[5].setSelectedIndex(1);
		inst_typebox[6].setSelectedIndex(20);
		inst_typebox[7].setSelectedIndex(3);

		inst_typebox[8].setSelectedIndex(4);
		inst_typebox[9].setSelectedIndex(0);
		inst_typebox[10].setSelectedIndex(1);
		inst_typebox[11].setSelectedIndex(2);

		inst_typebox[12].setSelectedIndex(3);
		inst_typebox[13].setSelectedIndex(4);
		inst_typebox[14].setSelectedIndex(3);
		inst_typebox[15].setSelectedIndex(1);

		inst_typebox[16].setSelectedIndex(5);
		inst_typebox[17].setSelectedIndex(5);
		inst_typebox[18].setSelectedIndex(0);
		inst_typebox[19].setSelectedIndex(3);

		inst_typebox[20].setSelectedIndex(2);
		inst_typebox[21].setSelectedIndex(3);
		inst_typebox[22].setSelectedIndex(4);
		inst_typebox[23].setSelectedIndex(1);

//执行时间设置
		tl1.setFont(new java.awt.Font("Dialog",0,fontsize));
		tl2.setFont(new java.awt.Font("Dialog",0,fontsize));
		tl3.setFont(new java.awt.Font("Dialog",0,fontsize));
		tl4.setFont(new java.awt.Font("Dialog",0,fontsize));
		tt1.setFont(new java.awt.Font("Dialog",0,fontsize));
		tt2.setFont(new java.awt.Font("Dialog",0,fontsize));
		tt3.setFont(new java.awt.Font("Dialog",0,fontsize));
		tt4.setFont(new java.awt.Font("Dialog",0,fontsize));
		EX_time_set_panel.add(tl1);
		EX_time_set_panel.add(tt1);
		EX_time_set_panel.add(tl2);
		EX_time_set_panel.add(tt2);
		EX_time_set_panel.add(tl3);
		EX_time_set_panel.add(tt3);
		EX_time_set_panel.add(tl4);
		EX_time_set_panel.add(tt4);

//指令状态设置
		for (int i=0;i<7;i++)
		{
			for (int j=0;j<4;j++){
				inst_typejl[i][j]=new JLabel(my_inst_type[i][j]);
				inst_typejl[i][j].setBorder(new EtchedBorder(EtchedBorder.RAISED));
				inst_typejl[i][j].setFont(new java.awt.Font("Dialog",0,fontsize));
				ins_state_panel.add(inst_typejl[i][j]);
			}
		}
//保留站设置
		for (int i=0;i<6;i++)
		{
			for (int j=0;j<8;j++){
				resjl[i][j]=new JLabel(my_rs[i][j]);
				resjl[i][j].setBorder(new EtchedBorder(EtchedBorder.RAISED));
				resjl[i][j].setFont(new java.awt.Font("Dialog",0,fontsize));
				RS_panel.add(resjl[i][j]);
			}
		}
//Load部件设置
		for (int i=0;i<4;i++)
		{
			for (int j=0;j<4;j++){
				ldjl[i][j]=new JLabel(my_load[i][j]);
				ldjl[i][j].setBorder(new EtchedBorder(EtchedBorder.RAISED));
				ldjl[i][j].setFont(new java.awt.Font("Dialog",0,fontsize));
				Load_panel.add(ldjl[i][j]);
			}
		}
//寄存器设置
		for (int i=0;i<3;i++)
		{
			for (int j=0;j<17;j++){
				regjl[i][j]=new JLabel(my_regsters[i][j]);
				regjl[i][j].setBorder(new EtchedBorder(EtchedBorder.RAISED));
				regjl[i][j].setFont(new java.awt.Font("Dialog",0,fontsize));
				Registers_state_panel.add(regjl[i][j]);
			}
		}

//向容器添加以上部件
		inst_typel.setFont(new java.awt.Font("Dialog",0,fontsize));
		ins_set_panel.setFont(new java.awt.Font("Dialog",0,fontsize));
		timel.setFont(new java.awt.Font("Dialog",0,fontsize));
		EX_time_set_panel.setFont(new java.awt.Font("Dialog",0,fontsize));
		startbut.setFont(new java.awt.Font("Dialog",0,fontsize));
		resetbut.setFont(new java.awt.Font("Dialog",0,fontsize));
		stepbut.setFont(new java.awt.Font("Dialog",0,fontsize));
		step5but.setFont(new java.awt.Font("Dialog",0,fontsize));
		Load_panel.setFont(new java.awt.Font("Dialog",0,fontsize));
		ldl.setFont(new java.awt.Font("Dialog",0,fontsize));
		RS_panel.setFont(new java.awt.Font("Dialog",0,fontsize));
		resl.setFont(new java.awt.Font("Dialog",0,fontsize));
		stepsl.setFont(new java.awt.Font("Dialog",0,fontsize));
		Registers_state_panel.setFont(new java.awt.Font("Dialog",0,fontsize));
		regl.setFont(new java.awt.Font("Dialog",0,fontsize));
		ins_set_panel.setFont(new java.awt.Font("Dialog",0,fontsize));
		insl.setFont(new java.awt.Font("Dialog",0,fontsize));


		cp.add(inst_typel);
		cp.add(ins_set_panel);
		cp.add(timel);
		cp.add(EX_time_set_panel);

		cp.add(startbut);
		cp.add(resetbut);
		cp.add(stepbut);
		cp.add(step5but);

		cp.add(Load_panel);
		cp.add(ldl);
		cp.add(RS_panel);
		cp.add(resl);
		cp.add(stepsl);
		cp.add(Registers_state_panel);
		cp.add(regl);
		cp.add(ins_state_panel);
		cp.add(insl);

		stepbut.setEnabled(false);
		step5but.setEnabled(false);
		ins_state_panel.setVisible(false);
		insl.setVisible(false);
		RS_panel.setVisible(false);
		ldl.setVisible(false);
		Load_panel.setVisible(false);
		resl.setVisible(false);
		stepsl.setVisible(false);
		Registers_state_panel.setVisible(false);
		regl.setVisible(false);
		setSize(2200,1200);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

/*
 * 点击”执行“按钮后，根据选择的指令，初始化其他几个面板
 */
	public void init(){
		// get value
		for (int i=0;i<6;i++){
			intv[i][0]=inst_typebox[i*4].getSelectedIndex();
			//intv[i][0] - 第i条指令的操作数
			/*		inst_type[]={"NOP","L.D","ADD.D","SUB.D","MULT.D","DIV.D"},
					regist_table[]={"F0","F2","F4","F6","F8","F10","F12","F14","F16"
							,"F18","F20","F22","F24","F26","F28","F30","F32"},
			*/
			//0: NOP, 1: L.D, 2: ADD.D, 3: SUB.D, 4: MULT.D, 5: DIV.D
			if (intv[i][0]!=0){
				intv[i][1]=2*inst_typebox[i*4+1].getSelectedIndex();
				//intv[i][1] - 第i条指令的目的操作数 F{intv[i][1]*2}, 0-32
				if (intv[i][0]==1){
					//若为load指令，则源操作数是立即数和通用寄存器
					//intv[i][2] - is(0-7), intv[i][3] - rs(R0-7)
					intv[i][2]=inst_typebox[i*4+2].getSelectedIndex();
					intv[i][3]=inst_typebox[i*4+3].getSelectedIndex();
				}
				else {
					intv[i][2]=2*inst_typebox[i*4+2].getSelectedIndex();
					intv[i][3]=2*inst_typebox[i*4+3].getSelectedIndex();
				}
			}
		}
		//time[0..3] - load，加减，乘法，除法的latency
		time[0]=Integer.parseInt(tt1.getText());
		time[1]=Integer.parseInt(tt2.getText());
		time[2]=Integer.parseInt(tt3.getText());
		time[3]=Integer.parseInt(tt4.getText());
		//System.out.println(time[0]);
		// set 0
		my_inst_type[0][0]="指令";
		my_inst_type[0][1]="流出";
		my_inst_type[0][2]="执行";
		my_inst_type[0][3]="写回";


		my_load[0][0]="名称";
		my_load[0][1]="Busy";
		my_load[0][2]="地址";
		my_load[0][3]="值";
		my_load[1][0]="Load1";
		my_load[2][0]="Load2";
		my_load[3][0]="Load3";
		my_load[1][1]="no";
		my_load[2][1]="no";
		my_load[3][1]="no";

		my_rs[0][0]="Time";
		my_rs[0][1]="名称";
		my_rs[0][2]="Busy";
		my_rs[0][3]="Op";
		my_rs[0][4]="Vj";
		my_rs[0][5]="Vk";
		my_rs[0][6]="Qj";
		my_rs[0][7]="Qk";
		my_rs[1][1]="Add1";
		my_rs[2][1]="Add2";
		my_rs[3][1]="Add3";
		my_rs[4][1]="Mult1";
		my_rs[5][1]="Mult2";
		my_rs[1][2]="no";
		my_rs[2][2]="no";
		my_rs[3][2]="no";
		my_rs[4][2]="no";
		my_rs[5][2]="no";

		my_regsters[0][0]="字段";
		for (int i=1;i<17;i++){
			//System.out.print(i+" "+regist_table[i-1];
			my_regsters[0][i]=regist_table[i-1];

		}
		my_regsters[1][0]="状态";
		my_regsters[2][0]="值";

		for (int i=1;i<7;i++)
		for (int j=0;j<4;j++){
			if (j==0){
				int temp=i-1;
				String disp;
				disp = inst_type[inst_typebox[temp*4].getSelectedIndex()]+" ";
				if (inst_typebox[temp*4].getSelectedIndex()==0) disp=disp;
				else if (inst_typebox[temp*4].getSelectedIndex()==1){
					disp=disp+regist_table[inst_typebox[temp*4+1].getSelectedIndex()]+','+ix[inst_typebox[temp*4+2].getSelectedIndex()]+'('+rx[inst_typebox[temp*4+3].getSelectedIndex()]+')';
				}
				else {
					disp=disp+regist_table[inst_typebox[temp*4+1].getSelectedIndex()]+','+regist_table[inst_typebox[temp*4+2].getSelectedIndex()]+','+regist_table[inst_typebox[temp*4+3].getSelectedIndex()];
				}
				my_inst_type[i][j]=disp;
			}
			else my_inst_type[i][j]="";
		}
		for (int i=1;i<6;i++)
		for (int j=0;j<8;j++)if (j!=1&&j!=2){
			my_rs[i][j]="";
		}
		for (int i=1;i<4;i++)
		for (int j=2;j<4;j++){
			my_load[i][j]="";
		}
		for (int i=1;i<3;i++)
		for (int j=1;j<17;j++){
			my_regsters[i][j]="";
		}
		inst_typenow=0;
		for (int i=0;i<5;i++){
			for (int j=1;j<3;j++) cal[i][j]=0;
			cal[i][0]=-1;
		}
		for (int i=0;i<3;i++)
			for (int j=0;j<2;j++) ld[i][j]=0;
		for (int i=0;i<17;i++) ff[i]=0;
		pg = new Program(6, intv, time);
	}

/*
 * 点击操作按钮后，用于显示结果
 */
	public void display(){
		for (int i=0;i<7;i++)
			for (int j=0;j<4;j++){
				inst_typejl[i][j].setText(my_inst_type[i][j]);//指令，流出，执行，写回
			}
		for (int i=0;i<6;i++)
			for (int j=0;j<8;j++){
				resjl[i][j].setText(my_rs[i][j]);//Time, 名称, busy, op, vj, vk, qj, qk
			}
		for (int i=0;i<4;i++)
			for (int j=0;j<4;j++){
				ldjl[i][j].setText(my_load[i][j]);//名称，busy，地址，值
			}
		for (int i=0;i<3;i++)
			for (int j=0;j<17;j++){
				regjl[i][j].setText(my_regsters[i][j]);//F0-30， 状态，值
			}
		stepsl.setText("当前周期："+String.valueOf(cnow-1));
	}

	public void actionPerformed(ActionEvent e){
//点击“执行”按钮的监听器
		if (e.getSource()==startbut) {
			for (int i=0;i<24;i++) inst_typebox[i].setEnabled(false);
			tt1.setEnabled(false);tt2.setEnabled(false);
			tt3.setEnabled(false);tt4.setEnabled(false);
			stepbut.setEnabled(true);
			step5but.setEnabled(true);
			startbut.setEnabled(false);
			//根据指令设置的指令初始化其他的面板
			init();
			cnow=1;
			//展示其他面板
			display();
			ins_state_panel.setVisible(true);
			RS_panel.setVisible(true);
			Load_panel.setVisible(true);
			Registers_state_panel.setVisible(true);
			insl.setVisible(true);
			ldl.setVisible(true);
			resl.setVisible(true);
			stepsl.setVisible(true);
			regl.setVisible(true);
		}
//点击“重置”按钮的监听器
		if (e.getSource()==resetbut) {
			for (int i=0;i<24;i++) inst_typebox[i].setEnabled(true);
			tt1.setEnabled(true);tt2.setEnabled(true);
			tt3.setEnabled(true);tt4.setEnabled(true);
			stepbut.setEnabled(false);
			step5but.setEnabled(false);
			startbut.setEnabled(true);
			ins_state_panel.setVisible(false);
			insl.setVisible(false);
			RS_panel.setVisible(false);
			ldl.setVisible(false);
			Load_panel.setVisible(false);
			resl.setVisible(false);
			stepsl.setVisible(false);
			Registers_state_panel.setVisible(false);
			regl.setVisible(false);
		}
//点击“步进”按钮的监听器
		if (e.getSource()==stepbut) {
			core();
			cnow++;
			display();
		}
//点击“进5步”按钮的监听器
		if (e.getSource()==step5but) {
			for (int i=0;i<5;i++){
				core();
				cnow++;
			}
			display();
		}

		for (int i=0;i<24;i=i+4)
		{
			if (e.getSource()==inst_typebox[i]) {
				if (inst_typebox[i].getSelectedIndex()==1){
					inst_typebox[i+2].removeAllItems();
					for (int j=0;j<ix.length;j++) inst_typebox[i+2].addItem(ix[j]);
					inst_typebox[i+3].removeAllItems();
					for (int j=0;j<rx.length;j++) inst_typebox[i+3].addItem(rx[j]);
				}
				else {
					inst_typebox[i+2].removeAllItems();
					for (int j=0;j<regist_table.length;j++) inst_typebox[i+2].addItem(regist_table[j]);
					inst_typebox[i+3].removeAllItems();
					for (int j=0;j<regist_table.length;j++) inst_typebox[i+3].addItem(regist_table[j]);
				}
			}
		}
	}
/*
 * (4)说明： Tomasulo算法实现
 */
	
	//time[0..3] - load，加减，乘法，除法的latency
	//intv[i][0] - 第i条指令的操作数
			/*		inst_type[]={"NOP","L.D","ADD.D","SUB.D","MULT.D","DIV.D"},
					regist_table[]={"F0","F2","F4","F6","F8","F10","F12","F14","F16"
							,"F18","F20","F22","F24","F26","F28","F30","F32"},
					rx[]={"R0","R1","R2","R3","R4","R5","R6"},
					ix[]={"0","1","2","3","4","5","6","7"};*/
			//0: NOP, 1: L.D, 2: ADD.D, 3: SUB.D, 4: MULT.D, 5: DIV.D

				//intv[i][1] - 第i条指令的目的操作数 F{intv[i][1]*2}, 0-32
					//若为load指令，则源操作数是立即数和通用寄存器
					//intv[i][2] - is(0-7), intv[i][3] - rs(R0-7)
					//若是计算指令，则源操作数是浮点寄存器，F0-32，同目标寄存器
	private class Program {
		public class Instr {
			public int issue_time;
			public int exe_time,exe_end_time;
			public int wb_time;
			public int status; //0 - not issue, 1 - issued, not exe, 2 - FP, 3 - Load1, 4 - Load2, 5 - wb, 6 - finished
			public int op, rt, rs1, rs2;
			public String result;
			public int res_station;
			public Instr(int op_index, int rt_index, int rs1_index, int rs2_index) {
				issue_time = -1;
				exe_time = -1; exe_end_time = -1;
				wb_time = -1;
				status = 0; rt = -1; rs1 = -1; rs2 = -1;
				op = op_index;//{"NOP","L.D","ADD.D","SUB.D","MULT.D","DIV.D"}
				if(op!=0) {
					rt = rt_index/2;
					rs1 = (op==1)?rs1_index:(rs1_index/2);
					rs2 = (op==1)?rs2_index:(rs2_index/2);
				}
				else {
					rt = 0; rs1 = 0; rs2 = 0;
				}
				result = "";
				res_station = 0;
			}
			
		}
		public class RS { //reservation station
			public int busy, op, qj, qk;
			public String vj, vk;
			public int remain_time;
			public RS() {
				busy = 0;
				op = 0;
				vj = ""; // 操作数的值
				vk = ""; // 操作数的值, M[x]-memory, R[x]-register
				qj = 0; //操作的保留站
				qk = 0; //操作的保留站
				remain_time = -1;
			}
		}
		public class LoadUnit {
			public int busy;
			public String address;
			public String value;
			public int remain_time;
			public LoadUnit() {
				busy = 0;
				address = "";
				value = "";
				remain_time = -1;
			}
		}
		public Instr instructions[];
		//3 loads, 3 adds, 2 muls
		public LoadUnit load[];
		public RS add[];
		public RS mult[];
		public int regstat[]; //0-2 load, 3-5 add, 6-7 mult
		public String regs[];
		public boolean available[];
		public int load_latency, add_latency, mult_latency, div_latency, instr_num;
		public boolean cdbfree;
		public Program(int inst_num, int inst_set[][], int time_set[])
		{
			cdbfree = true;
			instr_num = inst_num;
			instructions = new Instr[inst_num];
			for(int i=0;i<inst_num;i++) {
				instructions[i] = new Instr(inst_set[i][0],inst_set[i][1],inst_set[i][2],inst_set[i][3]);
			}
			load = new LoadUnit[3];
			add = new RS[3];
			mult = new RS[2];
			for(int i=0;i<3;i++) {
				load[i] = new LoadUnit();
				add[i] = new RS();
			}
			mult[0] = new RS(); mult[1] = new RS();
			regstat = new int[17];
			regs = new String[17];
			available = new boolean[17];
			for(int i=0;i<17;i++) {
				regstat[i] = 0;
				regs[i] = "R[F"+(i*2)+"]";
				available[i]=true;
			}
			load_latency = time_set[0];
			add_latency = time_set[1];
			mult_latency = time_set[2];
			div_latency = time_set[3];
		}

		public boolean try_issue(int ind) {
			if(instructions[ind].status != 0 || instructions[ind].op == 0) {
				return false;
			}
			int rt = instructions[ind].rt;
			//{"NOP","L.D","ADD.D","SUB.D","MULT.D","DIV.D"}
			//1-3 load, 4-6 add, 7-8 mult
			if(instructions[ind].op == 1) {//load
				int i;
				for(i=0;i<3;i++) {
					if(load[i].busy==0) {
						break;
					}
				}
				if(i==3) {
					return false;
				}
				regstat[rt] = i+1;
				instructions[ind].res_station = i;
				load[i].busy = 1;
				load[i].remain_time = load_latency;
				load[i].address = ""+(instructions[ind].rs1)+"";
			}
			else if(instructions[ind].op == 2 || instructions[ind].op == 3) {//add/sub
				int i;
				for(i=0;i<3;i++) {
					if(add[i].busy==0) {
						break;
					}
				}
				if(i==3) {
					return false;
				}
				int rs1 = instructions[ind].rs1;
				int rs2 = instructions[ind].rs2;
				int r1 = regstat[rs1]; int r2 = regstat[rs2];
				if(r1!=0 && ((r1<4 && load[r1-1].busy==1) || (r1<7 && r1>3 && add[r1-4].busy==1) || (r1>6 && mult[r1-7].busy==1))) {
					//1-3 load, 4-6 add, 7-8 mult
					add[i].qj = regstat[rs1];
				}
				else {
					add[i].vj = regs[rs1];
					add[i].qj = 0;
				}
				if(r2!=0 && ((r2<4 && load[r2-1].busy==1) || (r2<7 && r2>3 && add[r2-4].busy==1) || ( r2>6 && mult[r2-7].busy==1))) {
					add[i].qk = regstat[rs2];
				}
				else {
					add[i].vk = regs[rs2];
					add[i].qk = 0;
				}
				add[i].busy = 1;
				add[i].op = instructions[ind].op;
				if(add[i].qk==0 && add[i].qj==0) {
					add[i].remain_time = add_latency;
				}
				regstat[rt] = 4+i;
				instructions[ind].res_station = i;
				System.out.println("ISSUE: instr:"+i+" regstat"+(regstat[rt]));
			}
			else {
				int i;
				for(i=0;i<2;i++) {
					if(mult[i].busy==0) {
						break;
					}
				}
				if(i==2) {
					return false;
				}
				int rs1 = instructions[ind].rs1;
				int rs2 = instructions[ind].rs2;
				int r1 = regstat[rs1]; int r2 = regstat[rs2];
				if(r1!=0 && ((r1<4 && load[r1-1].busy==1) || (r1<7 && r1>3 && add[r1-4].busy==1) || (r1>6 && mult[r1-7].busy==1))) {
					//1-3 load, 4-6 add, 7-8 mult
					mult[i].qj = regstat[rs1];
				}
				else {
					mult[i].vj = regs[rs1];
					mult[i].qj = 0;
				}
				if(r2!=0 && ((r2<4 && load[r2-1].busy==1) || (r2<7 && r2>3 && add[r2-4].busy==1) || ( r2>6 && mult[r2-7].busy==1))) {
					mult[i].qk = regstat[rs2];
				}
				else {
					mult[i].vk = regs[rs2];
					mult[i].qk = 0;
				}
				mult[i].busy = 1;
				mult[i].op = instructions[ind].op;
				if(mult[i].qk==0 && mult[i].qj==0) {
					mult[i].remain_time = (instructions[ind].op==4)?mult_latency:div_latency;
				}
				regstat[rt] = 7+i;
				instructions[ind].res_station = i;
			}
			instructions[ind].issue_time = cnow;
			instructions[ind].status = 1;
			return true;
		}

		public void check_instr(int i) {
			//0 - not issue, 1 -  FP/Load1, 4 - Load2, 5 - wb, 6 - finished
			//{"NOP","L.D","ADD.D","SUB.D","MULT.D","DIV.D"}
			//1-3 load, 4-6 add, 7-8 mult
			int rt = instructions[i].rt;
			int rs1 = instructions[i].rs1;
			int rs2 = instructions[i].rs2;
			int r = instructions[i].res_station;
			int status = instructions[i].status;
			int op = instructions[i].op;
			System.out.println("instr:"+i+" regstat"+rt);
			System.out.println((regstat[rt]));
			if(status==0 || op==0) {
				return;
			}
			else if(status==1) {
				if(op==1) {//load
					instructions[i].status = 4;
					instructions[i].exe_time = cnow;
					//int r = r-1;
					load[r].address = "R[R"+rs2+"]+"+(load[r].address);
					load[r].remain_time = load[r].remain_time - 1;
				}
				else if(op==2 || op==3) {
					//int r = r-4;
					System.out.println("instruction"+i+" add/sub, r="+r);
					System.out.println("add[r].qj="+(add[r].qj)+" add[r].qk="+(add[r].qk));
					if(add[r].qj==0 && add[r].qk==0) {
						if(add[r].remain_time==-1) {
							if(available[rs1] && available[rs2]) {
								add[r].remain_time=add_latency;
							}
							else {
								return;
							}
						}
						if(add[r].remain_time==add_latency) {
							instructions[i].exe_time = cnow;
						}
						add[r].remain_time = add[r].remain_time - 1;
						if(add[r].remain_time == 0) {
							instructions[i].exe_end_time = cnow;
							instructions[i].status = 5;
						}
					}
				}
				else {
					//int r = r-7;
					if(mult[r].qj==0 && mult[r].qk==0) {
						if(mult[r].remain_time==-1) {
							if(available[rs1] && available[rs2]) {
								mult[r].remain_time=((op==4)?mult_latency:div_latency);
							}
							else {
								return;
							}
						}
						if(mult[r].remain_time==((op==4)?mult_latency:div_latency)) {
							instructions[i].exe_time = cnow;
						}
						mult[r].remain_time = mult[r].remain_time - 1;
						if(mult[r].remain_time==0) {
							instructions[i].exe_end_time = cnow;
							instructions[i].status = 5;
						}
					}
				}
			}
			else if(status==4) {
				//load 2
				//int r = r - 1;
				load[r].remain_time = load[r].remain_time - 1;
				if(load[r].remain_time==0) {
					load[r].value = "M["+(load[r].address)+"]";
					instructions[i].status = 5;
					instructions[i].exe_end_time = cnow;
				}
			}
			else if(status==5) {
				if(op==1) {
					//int r = regstat[rt]-1;
					int rs=r+1;
					if(cdbfree) {
						load[r].busy = 0;
						load[r].remain_time = -1;
						regs[rt] = "M["+(load[r].address)+"]";
						load[r].address="";
						load[r].value="";
						for(int j=0;j<3;j++) {
							if(add[j].qk==rs) {
								add[j].qk = 0;
								add[j].vk = regs[rt];
							}
							if(add[j].qj==rs) {
								add[j].qj = 0;
								add[j].vj = regs[rt];
							}
						}
						for(int j=0;j<2;j++) {
							if(mult[j].qk==rs) {
								mult[j].qk = 0;
								mult[j].vk = regs[rt];
							}
							if(mult[j].qj==rs) {
								mult[j].qj = 0;
								mult[j].vj = regs[rt];
							}
						}
						cdbfree = true;
						available[rt]=false;
						instructions[i].wb_time=cnow;
						instructions[i].status=6;
					}
				}
				else if(op==2||op==3) {
					//int r = regstat[rt]-4;
					if(cdbfree) {
						add[r].busy = 0;
						String r1 = add[r].vj;
						String r2 = add[r].vk;
						regs[rt] = (op==2)?(r1+"+"+r2):(r1+"-"+r2);
						int rs = r+4;
						for(int j=0;j<3;j++) {
							if(add[j].qk==rs) {
								add[j].qk = 0;
								add[j].vk = regs[rt];
							}
							if(add[j].qj==rs) {
								add[j].qj = 0;
								add[j].vj = regs[rt];
							}
						}
						for(int j=0;j<2;j++) {
							if(mult[j].qk==rs) {
								mult[j].qk = 0;
								mult[j].vk = regs[rt];
							}
							if(mult[j].qj==rs) {
								mult[j].qj = 0;
								mult[j].vj = regs[rt];
							}
						}
						cdbfree = true;
						instructions[i].wb_time=cnow;
						instructions[i].status=6;
						add[r].vj="";add[r].vk="";
						add[r].remain_time=-1;
						add[r].qj=0; add[r].qk=0; add[r].op=0;
						available[rt]=false;
					}
				}
				else {
					//int r = regstat[rt]-7;
					if(cdbfree) {
						mult[r].busy = 0;
						String r1 = "("+(mult[r].vj)+")";
						String r2 = "("+(mult[r].vk)+")";
						/*if(regstat[rs1]<7 && regstat[rs1]>3) {
							r1 = "("+r1+")";
						}
						if(regstat[rs2]<7 && regstat[rs2]>3) {
							r2 = "("+r2+")";
						}*/
						regs[rt] = (op==4)?(r1+"*"+r2):(r1+"/"+r2);
						int rs = r+7;
						for(int j=0;j<3;j++) {
							if(add[j].qk==rs) {
								add[j].qk = 0;
								add[j].vk = regs[rt];
							}
							if(add[j].qj==rs) {
								add[j].qj = 0;
								add[j].vj = regs[rt];
							}
						}
						for(int j=0;j<2;j++) {
							if(mult[j].qk==rs) {
								mult[j].qk = 0;
								mult[j].vk = regs[rt];
							}
							if(mult[j].qj==rs) {
								mult[j].qj = 0;
								mult[j].vj = regs[rt];
							}
						}
						cdbfree = true;
						instructions[i].wb_time=cnow;
						instructions[i].status=6;
						mult[r].vj="";mult[r].vk="";
						mult[r].remain_time=-1;
						mult[r].qj=0; mult[r].qk=0; mult[r].op=0;
						available[rt]=false;
					}
				}
			}
		}

		public void execute() {
			cdbfree = true;
			for(int i=0;i<6;i++) {
				if(instructions[i].status != 0 || instructions[i].op == 0) {
					if(instructions[i].status!=6 && instructions[i].op!=0) { //not finish yet
						check_instr(i);

					}
				}
				else {
					System.out.println("try issue"+i);
					boolean result = try_issue(i);
					System.out.println(""+result+"");
					break;
				}
			}
			for(int i=0;i<6;i++) {
				if(instructions[i].wb_time == cnow) {
					int rt=instructions[i].rt;
					available[rt]=true;
				}
			}	
		}
		public void display() {
			//my_inst_type[7][4] - 指令、流出、执行、写回
			//my_rs[6][8] - Time、名称、busy、op、vj、vk、qj、qk
			//my_load[4][4] - 名称、busy、地址、流
			//my_regsters[3][17] - 字段、状态、值 （F0-30）
			for(int i=0;i<6;i++) {
				switch(instructions[i].op) {
					//0 - not issue, 1 -  FP/Load1, 4 - Load2, 5 - wb, 6 - finished
					//{"NOP","L.D","ADD.D","SUB.D","MULT.D","DIV.D"}
					//1-3 load, 4-6 add, 7-8 mult
					case 0:  my_inst_type[i+1][0] = "NOP "; break;
					case 1:  my_inst_type[i+1][0] = "L.D "; break;
					case 2:  my_inst_type[i+1][0] = "ADD.D "; break;
					case 3:  my_inst_type[i+1][0] = "SUB.D "; break;
					case 4:  my_inst_type[i+1][0] = "MULT.D "; break;
					case 5:  my_inst_type[i+1][0] = "DIV.D "; break;
					default: my_inst_type[i+1][0] = "NOP "; break;
				}
				my_inst_type[i+1][0] = my_inst_type[i+1][0]+"F"+(2*instructions[i].rt)+", ";
				if(instructions[i].op==1){
					my_inst_type[i+1][0] = my_inst_type[i+1][0]+(instructions[i].rs1)+"(R"+(instructions[i].rs2)+")";
				}
				else {
					my_inst_type[i+1][0] = my_inst_type[i+1][0]+"F"+(2*instructions[i].rs1)+", F"+(2*instructions[i].rs2);
				}
				if(cnow==instructions[i].issue_time) {
					my_inst_type[i+1][1]=""+cnow+"";
				}
				else if(cnow==instructions[i].exe_time) {
					my_inst_type[i+1][2]=""+cnow+"~";
				}
				else if(cnow==instructions[i].exe_end_time) {
					my_inst_type[i+1][2]=my_inst_type[i+1][2]+cnow;
				}
				else if(cnow==instructions[i].wb_time) {
					my_inst_type[i+1][3]=""+cnow+"";
				}
			}
			for(int i=0;i<3;i++) {
				//load
				if(load[i].busy==1) {
					my_load[i+1][1]="Yes";
					my_load[i+1][2]=load[i].address;
					my_load[i+1][3]=load[i].value;
				}
				else {
					my_load[i+1][1]="No";
					my_load[i+1][2]=""; my_load[i+1][3]="";
				}
			}
			for(int i=0;i<3;i++) {
				if(add[i].busy==1) {
					my_rs[i+1][0]=(add[i].remain_time>0)?(""+add[i].remain_time):"";
					my_rs[i+1][2]="Yes";
					my_rs[i+1][3]=(add[i].op==2)?"ADD.D":"SUB.D";
					my_rs[i+1][4]=add[i].vj; my_rs[i+1][5]=add[i].vk;
					//1-3 load, 4-6 add, 7-8 mult
					if(add[i].qj==0) {
						my_rs[i+1][6]="";
					}
					else if(add[i].qj<4) {
						my_rs[i+1][6]="Load"+(add[i].qj);
					}
					else if(add[i].qj<7) {
						my_rs[i+1][6]="Add"+(add[i].qj-3);
					}
					else {
						my_rs[i+1][6]="Mult"+(add[i].qj-6);
					}
					if(add[i].qk==0) {
						my_rs[i+1][7]="";
					}
					else if(add[i].qk<4) {
						my_rs[i+1][7]="Load"+(add[i].qk);
					}
					else if(add[i].qk<7) {
						my_rs[i+1][7]="Add"+(add[i].qk-3);
					}
					else {
						my_rs[i+1][7]="Mult"+(add[i].qk-6);
					}
				}
				else {
					my_rs[i+1][2]="No";
					for(int j=3;j<7;j++) {
						my_rs[i+1][j]="";
					}
				}
			}
			for(int i=0;i<2;i++) {
				if(mult[i].busy==1) {
					my_rs[i+4][0]=(mult[i].remain_time>0)?(""+mult[i].remain_time):"";
					my_rs[i+4][2]="Yes";
					my_rs[i+4][3]=(mult[i].op==4)?"MULT.D":"DIV.D";
					my_rs[i+4][4]=mult[i].vj; my_rs[i+4][5]=mult[i].vk;
					//1-3 load, 4-6 add, 7-8 mult
					if(mult[i].qj==0) {
						my_rs[i+4][6]="";
					}
					else if(mult[i].qj<4) {
						my_rs[i+4][6]="Load"+(mult[i].qj);
					}
					else if(mult[i].qj<7) {
						my_rs[i+4][6]="Add"+(mult[i].qj-3);
					}
					else {
						my_rs[i+4][6]="Mult"+(mult[i].qj-6);
					}
					if(mult[i].qk==0) {
						my_rs[i+4][7]="";
					}
					else if(mult[i].qk<4) {
						my_rs[i+4][7]="Load"+(mult[i].qk);
					}
					else if(mult[i].qk<7) {
						my_rs[i+4][7]="Add"+(mult[i].qk-3);
					}
					else {
						my_rs[i+4][7]="Mult"+(mult[i].qk-6);
					}
				}
				else {
					my_rs[i+4][2]="No";
					for(int j=3;j<7;j++) {
						my_rs[i+4][j]="";
					}
				}
			}
			for(int i=0;i<16;i++) {
				if(regstat[i]==0) {
					my_regsters[1][i+1]="";
					continue;
				}
				else if(regstat[i]<4) {
					my_regsters[1][i+1]="Load"+(regstat[i]);
				}
				else if(regstat[i]<7) {
					my_regsters[1][i+1]="Add"+(regstat[i]-3);
				}
				else {
					my_regsters[1][i+1]="Mult"+(regstat[i]-6);
				}
				my_regsters[2][i+1] = regs[i];
			}
		}
	}
	Program pg;
	public void core()
	{
		//one step
		pg.execute();
		pg.display();
	}

	public static void main(String[] args) {
		new Tomasulo();
	}

}
