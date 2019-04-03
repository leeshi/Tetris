package io.leeshi.tetris;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

public class Tetris extends JPanel{
	private static final long serialVersionUID = 1L;
	private static final int HEIGHT = 20;
	private static final int WIDTH = 10;
	private static final int TIME_DELAY = 300;
	private static final int BLOCK_SIZE = 50;
	
	private static boolean[][][] shape = Block.SHAPE;
	
	//����Ѿ��̶��ķ���
	private boolean[][] BlockMap;
	//��ŵ�ǰ�ķ���
	private boolean[][] NowBlockMap;
	//�����һ״̬����
	private boolean[][] NextBlockMap;
	
	
	//��28-(0,27)
	private int NowBlockState;
	private int NextBlockState;
	//mark the left up position��swingΪ��������ϵ
	private Point NowBlockPos;
	
	//��ʱ��
	private Timer timer;
	
	private boolean GameOver = false;
	public Tetris() {
		super();
		this.init();
		this.addKeyListener(this.keyListener);
		
		this.BlockMap[Tetris.HEIGHT - 1] = new boolean[] {true,true,true,true,true,true,true,true,true,true};
		this.BlockMap[Tetris.HEIGHT - 2] = new boolean[] {true,true,true,true,true,true,true,true,true,true};
		this.BlockMap[Tetris.HEIGHT - 3] = new boolean[] {true,true,true,true,true,true,true,true,true,true};
		this.BlockMap[Tetris.HEIGHT - 4] = new boolean[] {true,true,true,true,true,true,true,true,true,true};
		
		timer = new Timer(Tetris.TIME_DELAY, this.TimerListener);
		timer.start();
	}
	
	private void init() {			
		this.BlockMap = new boolean[Tetris.HEIGHT][Tetris.WIDTH];
		
		
		this.NextBlockMap = Tetris.shape[(int) Math.round(Math.random() * 6)];
		this.GetNextBlockMap();
		this.GameOver = false;
		
	}
	
	/*
	 * �����µ���ʼ��λ��
	 */
	private Point GetNewPos() {
		return new Point(Tetris.WIDTH / 2,- this.NowBlockMap.length);
	}
	
	/*
	 * ׼��������һ����״̬,���µķ�������ʱ
	 */
	private void GetNextBlockMap() {
		this.NowBlockState = this.NextBlockState;
		this.NextBlockState = this.CreateNewBlockState();
		
		this.NowBlockMap = this.NextBlockMap;
		this.NextBlockMap = this.GetNewBlockMap(this.NextBlockState);
		
		this.NowBlockPos = this.GetNewPos();
	}
	
	/*
	 * ��������µķ���״̬
	 */
	private int CreateNewBlockState() {
		int sum = shape.length * 4;
		return (int)(Math.random()*1000%sum);//����һ�������
	}
	
	/*
	 * �̶����鵽BlockMap��
	 */
	private boolean FixBlock() {
		for(int i = this.NowBlockMap.length - 1;i >= 0;i--) {
			for(int j = 0;j < this.NowBlockMap[0].length;j++) {
				if (this.NowBlockMap[i][j])
					if (this.NowBlockPos.y + i < 0) {
						if(this.BlockMap[0][this.NowBlockPos.x + j]&&this.NowBlockMap[i][j]) {
							this.GameOver = true;
							return true;
						}
					}
					else
						this.BlockMap[this.NowBlockPos.y + i][this.NowBlockPos.x + j] = this.NowBlockMap[i][j];
			}
		}
		
		return true;
	}
	
	/*
	 * ���ݷ���״̬�����µ�BlockMap���ܹ���4����ת��� 0 1 2 3
	 * @return
	 * 			��ת��ľ���
	 */
	private boolean[][] GetNewBlockMap(int BlockState){
		int shape = BlockState / 4;
		int arc = BlockState % 4;
		System.out.println("BlockState:"+(BlockState/4)+arc);
		return Rotate(Tetris.shape[shape],arc);
	}
	
	/*
	 * ���ݼ�����ת��������ÿһ���������ת
	 *  				x=cos(��) y=sin(��)
	 * 
	 * @param BlockShape
	 * 			��Ҫ��ת����״
	 * @param arc
	 * 			��Ҫ��ת�ĽǶ�,0 1 2 3 ���� 0 pi/2 pi (3/2)*pi
	 * @return  ��ת��ľ���
	 */
	private boolean[][] Rotate(boolean[][] BlockShape,int arc){
		//��ȡ��״�ĳ���
		int height = BlockShape.length;
		int width = BlockShape[0].length;
		//������ת���
		boolean[][] RotatedShape = new boolean[height][width];
		
		//����õ���ת���������,Ϊ�˱�֤ÿһ���㶼�������������ת��ʹ��float
		//����Ϊ�����0��ʼ���㣬�����Ҫ-1,����Ǵ�1��ʼ�ľͲ���Ҫ��
		float CenterX = (width - 1) / 2f;
		float CenterY = (height - 1) / 2f;
		
		//������任λ��
		for(int i = 0;i < height;i++)
			for(int j = 0;j < width;j++){
				if(BlockShape[i][j]) {
					float RelativeX = j - CenterX;
					float RelativeY = i - CenterY;
					
					float X = (float) (RelativeX * Math.cos(Math.PI/2*arc) - RelativeY * Math.sin(Math.PI/2*arc));
					float Y = (float) (RelativeY * Math.cos(Math.PI/2*arc) + RelativeX * Math.sin(Math.PI/2*arc));
					
					System.out.println("("+RelativeX+","+RelativeY+") => ("+X+","+Y+")");
					
					Point point = new Point(Math.round(X + CenterX),Math.round(Y + CenterY));
					
					RotatedShape[point.y][point.x] = BlockShape[i][j];
				}
			}
		return RotatedShape;
	}
	
	/*
	 * �ж�����ķ����Ƿ���ǽ�ڣ��̶�������Ӵ�
	 * @param
	 * 			��ʼ�������ת����  , Ŀ�ĵ�
	 * @return 
	 */
	private boolean isTouch(boolean[][] ScanNowBlockMap,Point DescPoint) {
		//��ǰ�����й̶�����
		for(int i = 0;i < ScanNowBlockMap.length;i++) {
			for(int j = 0;j < ScanNowBlockMap[0].length;j++) {
				if(ScanNowBlockMap[i][j]) {
					//��ȵ�ʱ���Ѿ�����,������������
					if(DescPoint.y + i >= HEIGHT || DescPoint.x + j >= WIDTH || DescPoint.x + j < 0)
						return true;
					else{
						//��̶��ķ�����бȽ�
						
						if(DescPoint.y + i < 0)
							continue;
						else {
							if(this.BlockMap[DescPoint.y + i][DescPoint.x + j]) {
								return true;
								}
						}
					}
				}
			}
		}
		return false;
	}
	
	/*
	 * �����û���������
	 */
	private void ClearLines() {
		for(int i = Tetris.HEIGHT - 1;i >= 0 ;i--) {
			boolean flag = true;
			for(int j = 0;j < Tetris.WIDTH;j++) {
				if(this.BlockMap[i][j])
					continue;
				else {
					flag = false;
					break;
				}
			}
			
			//��ʼ����
			if(flag) {
				System.out.println("The "+(Tetris.HEIGHT - i)+"th has been removed");
				for(int j = i;j > 0;j--) {
					this.BlockMap[j] = this.BlockMap[j - 1];
				}
				this.BlockMap[0] = new boolean[Tetris.WIDTH];
				
				//��������¶�i���и�ֵ�����޷�ͬʱ��������
				i = Tetris.HEIGHT;
			}
		}
	}
	
	/*
	 * ���Ʒֽ��ߣ���ǰ�����Լ��Ѿ��̶��ķ���
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 * @TODO δ������һ���������ʾ
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		//���Ʒֽ���
		for(int i = 0;i < this.BlockMap.length;i++) {
			g.drawRect(0 * Tetris.BLOCK_SIZE, i * Tetris.BLOCK_SIZE, Tetris.BLOCK_SIZE * Tetris.WIDTH, Tetris.BLOCK_SIZE);
		}
		for(int i = 0;i < this.BlockMap[0].length;i++) {
			g.drawRect(i * Tetris.BLOCK_SIZE, 0 * Tetris.BLOCK_SIZE, Tetris.BLOCK_SIZE, Tetris.BLOCK_SIZE * Tetris.HEIGHT);
		}
		
		//������ʾ���鼰��ֽ���
		for(int i = 0;i < this.NextBlockMap.length;i++) {
			for(int j = 0;j < this.NextBlockMap.length;j++){
				if(this.NextBlockMap[i][j])
					g.fillRect((j + 2 + Tetris.WIDTH) * Tetris.BLOCK_SIZE, (i + 5) * Tetris.BLOCK_SIZE, Tetris.BLOCK_SIZE, Tetris.BLOCK_SIZE);
				else
					g.drawRect((j + 2 + Tetris.WIDTH) * Tetris.BLOCK_SIZE, (i + 5) * Tetris.BLOCK_SIZE, Tetris.BLOCK_SIZE, Tetris.BLOCK_SIZE);
			}
		}
		
		//���Ƶ�ǰ����
		for(int i = 0;i < this.NowBlockMap.length;i++) {
			for(int j = 0;j < this.NowBlockMap[0].length;j++) {
				if(this.NowBlockMap[i][j]){
					g.fillRect((this.NowBlockPos.x + j) * Tetris.BLOCK_SIZE,(this.NowBlockPos.y + i) * Tetris.BLOCK_SIZE, Tetris.BLOCK_SIZE, Tetris.BLOCK_SIZE);
				}
			}
		}
		
		//�����Ѿ��̶��ķ���
		for(int i = 0;i < this.BlockMap.length;i++) {
			for(int j = 0;j < this.BlockMap[0].length;j++) {
				if(this.BlockMap[i][j])
					g.fillRect(j * Tetris.BLOCK_SIZE, i * Tetris.BLOCK_SIZE, Tetris.BLOCK_SIZE, Tetris.BLOCK_SIZE);
			}
		}
	}
	
	private ActionListener TimerListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("1000ms has passed");
			Point DescPoint = new Point(Tetris.this.NowBlockPos.x,Tetris.this.NowBlockPos.y + 1);
			
			if(isTouch(Tetris.this.NowBlockMap,DescPoint)) {
				if(FixBlock()) {
					System.out.println("BlockMap has been fixed");
					ClearLines();
					GetNextBlockMap();
					if(Tetris.this.GameOver) {
						Tetris.this.init();
						JOptionPane.showMessageDialog(Tetris.this.getParent(),"GAME OVER");
					}
				}
				else {
					;
				}
			}
			else {
				Tetris.this.NowBlockPos = DescPoint;
			}
			Tetris.this.repaint();
		}
		
	};
	
	
	//��������
	private KeyListener keyListener = new KeyListener() {

		@Override
		public void keyTyped(KeyEvent e) {
			//TODO
		}

		@Override
		public void keyPressed(KeyEvent e) {
			Point DescPoint;
			switch(e.getKeyCode()) {
				case KeyEvent.VK_DOWN:
					DescPoint = new Point(Tetris.this.NowBlockPos.x,( Tetris.this.NowBlockPos.y + 1));  //����yӦ�ü�1
					if(!Tetris.this.isTouch(Tetris.this.NowBlockMap,DescPoint)) {
						Tetris.this.NowBlockPos = DescPoint;
						Tetris.this.repaint();
					}
					//������Ϣ
					System.out.println("VK_DOWN pressed");
				break;
				
				case KeyEvent.VK_LEFT:
					DescPoint = new Point(Tetris.this.NowBlockPos.x - 1,Tetris.this.NowBlockPos.y);
					if(!isTouch(Tetris.this.NowBlockMap,DescPoint)) {
						Tetris.this.NowBlockPos = DescPoint;
						Tetris.this.repaint();
					}
					System.out.println("VK_LEFT pressed");
				break;
				
				case KeyEvent.VK_RIGHT:
					DescPoint = new Point(Tetris.this.NowBlockPos.x + 1,Tetris.this.NowBlockPos.y);
					if(!isTouch(Tetris.this.NowBlockMap,DescPoint)) {
						Tetris.this.NowBlockPos = DescPoint;
						Tetris.this.repaint();
					}
					System.out.println("VK_RIGHT pressed");
				break;
				case KeyEvent.VK_UP:
					boolean[][] RotatedBlockMap = Tetris.this.Rotate(Tetris.this.NowBlockMap, 1);
					if(!isTouch(RotatedBlockMap,Tetris.this.NowBlockPos)) {
						Tetris.this.NowBlockMap = RotatedBlockMap;
						Tetris.this.repaint();
					}
					System.out.println("VK_UP pressed");
				break;
				default:System.out.println(e.getKeyChar());
				
			}			
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	public static void main(String...args) {
		Tetris test = new Tetris();
		JFrame frame = new JFrame();
		test.setFocusable(true);
		frame.add(test);
		frame.setVisible(true);
		frame.setSize(1200, 1200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
}
