package edu.bupt.ipoc.service;

import java.util.ArrayList;
import java.util.List;

import edu.bupt.ipoc.service.*;

public class VirtualTransLink extends Service{
	
	public static final int BUILD_REQUEST = 1;
	public static final int REMOVE_REQUEST = 2;
	public static final int EXTEND_REQUEST = 3;
	
	
	//The highest priority of carried packet service.
	public static final int VTL_P_HIGH = 1;
	public static final int VTL_P_MID = 2;
	public static final int VTL_P_LOW = 3;
	
	//
	public static final int VTL_MAX_CARRIED_PS_HIGH = 100;
	public static final int VTL_MAX_CARRIED_PS_MID = 50;
	public static final int VTL_MAX_CARRIED_PS_LOW = 20;
	
	//test
	public static final int CAN_NOT_BE_EXTEND_BUT_SHARE = 1;
	
	//
	public static final int BW_EMPTY = 0;
	
	public int id;
	public int sourceVertex;
	public int destVertex;	
	
	public int vtl_priority = VTL_P_LOW;
	
	public int max_carried_ps = VTL_MAX_CARRIED_PS_LOW;
	
	public int bw_capacity = BW_EMPTY;// the upper is 4Z bit, so int is enough right now.
	
	public List<PacketService> carriedPacketServices = null;
	
	public List<OTNService> relevantOTNServices = null;
	public List<OpticalService> relevantOpticalServices = null;
	
	public VirtualTransLink()
	{
		carriedPacketServices = new ArrayList<PacketService>();
		
		relevantOTNServices = new ArrayList<OTNService>();	
		relevantOpticalServices = new ArrayList<OpticalService>();		
	}
	
	public void setID(int _id)
	{
		this.id = _id;
	}
	
	public void setSourceAndDest(int _source, int _dest)
	{
		this.sourceVertex = _source;
		this.destVertex = _dest;
	}
	
	public void setPrioriy(int _priority)
	{
		if(_priority < this.vtl_priority)//higher
			this.vtl_priority = _priority;
	}
	
	public void addRelevantOpticalService(OpticalService _os)
	{
		this.relevantOpticalServices.add(_os);
		
		this.updateCapacity();
		//this.allBW += _os.get
	}
	
	public void addRelevantOTNService(OTNService _otns)
	{
		this.relevantOTNServices.add(_otns);
		
		this.updateCapacity();
		//this.allBW += _os.get
	}
	
	public int updateCapacity()
	{
		int _bw_capacity = 0;
		if(relevantOTNServices.size()>0)
		{
			for(OTNService otns : relevantOTNServices)
			{
				_bw_capacity += otns.getBWCapacity();
			}
		}
		
		if(relevantOpticalServices.size() > 0)
		{
			
			for(OpticalService os : relevantOpticalServices)
			{
				_bw_capacity += os.getBWCapacity();
			}
			
			
		}
		
		bw_capacity = _bw_capacity;
		return bw_capacity;		
	}
	
	public boolean canCarryMorePS()
	{
		if(this.carriedPacketServices.size() < this.max_carried_ps)
			return true;
		return false;
	}
	
	public boolean canOfferMoreBW(int _bw)
	{
		if(carriedPacketServices.size()>0)
		{	
			if((getUsedBWofVTL() + _bw) < getMaxBWCanCarried())
				return true;			
		}		
		return false;
	}
	
	public int getRestBW()
	{
		return getMaxBWCanCarried() - getUsedBWofVTL();
	}
	
	public int getMaxBWCanCarried()
	{
		return this.bw_capacity;
	}
	
/*	public int canBeExtended()
	{
		return false;
	}
*/	
	public void removeRelevantOpticalService(OpticalService _os)
	{
		this.relevantOpticalServices.remove(_os);
		//this.allBW += _os.get
	}
	
	public void addPacketServiceToCarry(PacketService _ps)
	{
		this.carriedPacketServices.add(_ps);
	}
	
	public void removeCarriedPacketService(PacketService _ps)
	{
		this.carriedPacketServices.remove(_ps);
	}
	
	/*
	public long getBWofVTL()
	{
		if(relevantOpticalServices.size()>0)
		{
			long _allBW = 0;
			
			for(OpticalService _os : relevantOpticalServices)
			{
				
			}
			this.allBW = _allBW;
			
			return _allBW;		
		}
		else
			return 0;
	}*/
	
	public int getUsedBWofVTL()
	{
		if(carriedPacketServices.size()>0)
		{
			int usedBW = 0;
			
			for(PacketService _ps : carriedPacketServices)
			{
				usedBW += _ps.getCurrentBw();
			}
			return usedBW;
		}
		else
			return 0;
	}
	
	public String toString()
	{
		String describtion = new String();
		describtion += "The vtl id:"+this.id;
		describtion += "\tthe priority:"+this.vtl_priority;
		describtion += "\n\tCarried ps num:"+carriedPacketServices.size();
		
		int request_bw = 0;
		for(PacketService _ps : carriedPacketServices)
		{
			//describtion += "\n\t\t ps id:"+_ps.id+"\t the request bw is:"+_ps.getCurrentBw();
			request_bw += _ps.getCurrentBw();
		}
		describtion += "\n\t\t all ps request bw is :"+request_bw;
		if(this.relevantOpticalServices.size()>0)
		{
			describtion += "\n\tUsed os num:"+relevantOpticalServices.size();
			for(OpticalService _os : relevantOpticalServices)
			{
				describtion += "\n\t\t os id:"+_os.id;
			}
		}
		if(this.relevantOTNServices.size()>0)
		{
			describtion += "\n\tUsed otn num:"+relevantOTNServices.size();
			if((relevantOTNServices.size()-1) * OTNService.BW_1G > request_bw)
				System.out.println("************************************************************");
			for(OTNService _otns : relevantOTNServices)
			{
				describtion += "\n\t\t otn id:"+_otns.id;
				describtion += "; carried os id:"+_otns.osBelongTo.id;
			}
		}
		
		return describtion;
		//System.out.println("vtl id:"+vtl.id+"\tthe priority:"+vtl.vtl_priority+"\tCarried packet services:"+vtl.carriedPacketServices.size());
		//System.out.println("")
	}
	

}
