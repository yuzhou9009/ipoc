package edu.bupt.ipoc.resource;



public class OpticalLink {
	
	public static final int SLOTS = 20;
	
	public static final int FREE_FLAG = 0;
	public static final int INIT_COLOR = 1;
	public double weight;
	
	public int[] slots;
	
	private int slots_empty_num = SLOTS;
	
	private int start_index;
	private int end_index;
	
	public OpticalLink()
	{
		slots = new int[SLOTS];
		for(int i =0 ; i<SLOTS ;i++)
			slots[i] = FREE_FLAG;
	}
	
	public void clear_slots()
	{
		for(int i =0 ; i<SLOTS ;i++)
			slots[i] = FREE_FLAG;
	}
	
	public void set_weight(double we)
	{
		weight = we;
	}
	
	public int count_slots_num()
	{
		int i =0;
		for(int j = 0;j<SLOTS;j++)
		{	
			if(slots[j] == FREE_FLAG)
				i++;
		}
		return i;
	}
	
	public int get_slots_num()
	{
		return slots_empty_num;
	}

	public int getStart_index() {
		return start_index;
	}

	public void setStart_index(int start_index) {
		this.start_index = start_index;
	}

	public int getEnd_index() {
		return end_index;
	}

	public void setEnd_index(int end_index) {
		this.end_index = end_index;
	}
}
