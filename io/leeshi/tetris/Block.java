package io.leeshi.tetris;

/*
 * ����������еķ���
 * Ϊ�˷����ж���ת�����еķ��鶼�Ƿ���
 */
public class Block {
	
	/*
	 * 0000
	 */
	public static final boolean [][][] SHAPE = {
		{
			{false,true,false,false},
			{false,true,false,false},
			{false,true,false,false},
			{false,true,false,false}
	},
	
	/* 00
	 *  00
	 */
	{
			{true,true,false},
			{false,true,true},
			{false,false,false}
	},
	
	/*
	 * 00
	 * 00
	 */
	{
			{true,true},
			{true,true}
	},
	
	/*  000
	 *   0
	 *   0
	 */ 
	{
			{true,true,true},
			{false,true,false},
			{false,false,false}
	},
	
	/*  0
	 *  0
	 *  00
	 */
	{
			{true,false,false},
			{true,false,false},
			{true,true,false}
	},
	
	/*   0
	 *   0
	 *  00
	 */
	{
			{false,false,true},
			{false,false,true},
			{false,true,true}
	},
	
	
	/*  00
	 * 00
	 */
	{
			{false,true,true},
			{true,true,false},
			{false,false,false}
		}
	};

}