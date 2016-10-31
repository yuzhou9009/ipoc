package edu.bupt.ipoc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.bupt.ipoc.constraint.Constraint;
import edu.bupt.ipoc.service.*;

public class VirtualTransLink extends Service{
	
	public static final int BUILD_REQUEST = 1;
	public static final int REMOVE_REQUEST = 2;
	public static final int EXTEND_REQUEST = 3;
	public static final int SHRINKED_REQUEST = 4;
	//
	public static final int VTL_MAX_CARRIED_PS_HIGH = 100;
	public static final int VTL_MAX_CARRIED_PS_MID = 50;
	public static final int VTL_MAX_CARRIED_PS_LOW = 20;
	
	//test
	public static final int CAN_NOT_BE_EXTENDED_OR_SHARED = 0;
	public static final int CAN_NOT_BE_EXTENDED_BUT_SHARED = 1;//traditional;vtl can be shared with other services, but once the bw is not enough, it will caused conflict, then the affiliated service need to move to other vtl or need a new vtl for being carried.
	public static final int VTL_BOD = 2;
	
	public static final int NOT_NEED_ADJUSTED = 0;
	public static final int NEED_TO_BE_EXTENDED = 1;
	public static final int NEED_TO_BE_SHRINKED = 2;
	
	//these four threshold values can be changed on-line. But these are used statically right now.
	public double th_usp_high = 0.75;
	public double th_usp_mid = 0.85;
	public double th_usp_low = 0.95;	
	public double th_lsp_all = 0.4;
	
	//
	public static final int BW_EMPTY = 0;
	
	public int id;
	public int sourceVertex;
	public int destVertex;	
	
	public int vtl_priority = PRIORITY_LOW;
	
	public int max_carried_ps = VTL_MAX_CARRIED_PS_LOW;
	
	public int bw_capacity = BW_EMPTY;// the upper is 4Z bit, so int is enough right now.
	
	public int type  = CAN_NOT_BE_EXTENDED_OR_SHARED;
	//public boolean bod_on = false;
	
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
		//if(_priority < this.vtl_priority)//higher
		this.vtl_priority = _priority;
	}
	
	public void setType(int _type)
	{
		this.type = _type;
	}
	

	public void setTypeAccordingToConstraints(Map<Integer, Constraint> cons) {
		if(cons != null)
		{
			Constraint _con = cons.get(Constraint.VTL_CARRY_TYPE_C);
			if(_con != null)
			{
				setType(_con.value);
			}
		}
		else
		{
			System.out.println("The vtl carried type is not be defined.");
			setType(VirtualTransLink.CAN_NOT_BE_EXTENDED_BUT_SHARED);
		}
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
	
	public double getCurrentHighThresholdValue()
	{
		if(vtl_priority == PRIORITY_HIGH)
			return th_usp_high;
		else if(vtl_priority == PRIORITY_MID)
			return th_usp_mid;
		else if(vtl_priority == PRIORITY_LOW)
			return th_usp_low;
		System.out.println("Big bug there shold have a priority for VTL!");
		return -1;
	}
	
	public double getCurrentLowThresholdValue()
	{
		return this.th_lsp_all;
		/*
		if(vtl_priority == VTL_P_HIGH)
			return th_usp_high;
		else if(vtl_priority == VTL_P_MID)
			return th_usp_mid;
		else if(vtl_priority == VTL_P_LOW)
			return th_usp_low;
		System.out.println("Big bug there shold have a priority for VTL!");
		return -1;*/
	}
	
	public boolean canOfferMoreBW(int _bw)//just offer bw for the same priority
	{
		if(carriedPacketServices.size()>0)
		{
			if(this.type == VirtualTransLink.CAN_NOT_BE_EXTENDED_OR_SHARED)
			{
				System.out.println("We may never have a chance to get here!");
				return false;
			}
			else if(this.type == VirtualTransLink.CAN_NOT_BE_EXTENDED_BUT_SHARED)
			{
				if((getUsedBWofVTL() + _bw) * SURVIVABILITY_FACTOR <= getCapacity())
					return true;
			}
			else if(this.type == VirtualTransLink.VTL_BOD)
			{
				double _tem = 1.0/getCurrentHighThresholdValue();
				
				if((getUsedBWofVTL() + _bw) * _tem <= getCapacity())
					return true;
			}
		}		
		return false;
	}
	
	public int getRestBW()//need to be modified
	{
		if(this.type == VirtualTransLink.VTL_BOD)
		{
			return (int)(getCapacity() * getCurrentHighThresholdValue()) - getUsedBWofVTL();
		}
		else if (this.type == VirtualTransLink.CAN_NOT_BE_EXTENDED_OR_SHARED || this.type == VirtualTransLink.CAN_NOT_BE_EXTENDED_BUT_SHARED)
			return (int)(getCapacity() * REGULAR_UPPER_THRESHOLD) - getUsedBWofVTL();
		else
			System.out.println("Never would be here!");
		return (int)(getCapacity() * REGULAR_UPPER_THRESHOLD) - getUsedBWofVTL();
	}
	
	public int getAcutallyRestBWforShare()
	{
		//The priority of vtl should not be low, and the type of vtl should be VTL_BOD 
		//if(vtl_priority == PRIORITY_LOW || type != VTL_BOD)
		//{
		//	System.out.println("It should never be here!!");
		//	return 0;
		//}
		//int _tem  = ((int)(getCapacity() * th_usp_low - getUsedBWofVTL())/MIN_SHARED_BW_GRANULARITY)*MIN_SHARED_BW_GRANULARITY;
		//System.out.println("orginal:"+(int)(getCapacity() * th_usp_low - getUsedBWofVTL())+"_tem"+_tem);
		if(this.vtl_priority != Service.PRIORITY_LOW)
			return (((int)(getCapacity() * th_usp_low) - getUsedBWofVTL())/MIN_SHARED_BW_GRANULARITY)*MIN_SHARED_BW_GRANULARITY;
		else
			return (int)(getCapacity() * th_usp_low) - getUsedBWofVTL();
	}
	
	public int getAcutallyRestBWforShareNoLimit()
	{
		//The priority of vtl should not be low, and the type of vtl should be VTL_BOD 
		//if(vtl_priority == PRIORITY_LOW || type != VTL_BOD)
		//{
		//	System.out.println("It should never be here!!");
		//	return 0;
		//}
		//int _tem  = ((int)(getCapacity() * th_usp_low - getUsedBWofVTL())/MIN_SHARED_BW_GRANULARITY)*MIN_SHARED_BW_GRANULARITY;
		//System.out.println("orginal:"+(int)(getCapacity() * th_usp_low - getUsedBWofVTL())+"_tem"+_tem);
		return ((int)(getCapacity() * th_usp_low) - getUsedBWofVTL());
	}

	public int getCapacity()
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
				usedBW += _ps.getCurrentOccupiedBw();
			}
			return usedBW;
		}
		else
			return 0;
	}
	
	
	public int getUsedBWofVTLByThisPriority()
	{
		if(carriedPacketServices.size()>0)
		{
			int usedBW = 0;
			
			for(PacketService _ps : carriedPacketServices)
			{
				if(_ps.priority == this.vtl_priority)
					usedBW += _ps.getCurrentOccupiedBw();
			}
			return usedBW;
		}
		else
			return 0;
	}
	/*	
	public boolean BWBeyondWholeHighThresholdValue()
	{
		if(getAcutallyRestBW()<0)
			return true;
		return false;
	}
	
	public boolean BWBeyondSpecificPriorityHighThresholdValue()
	{
		int _bw = getUsedBWofVTLByThisPriority();
		if(_bw >= getCurrentHighThresholdValue() * getMaxBWCanCarried())
			return true;
		return false;
	}


	public int needCapacityAdjustment() {
		// TODO Auto-generated method stub
		if(BWBeyondWholeHighThresholdValue())
		{
			if(BWBeyondSpecificPriorityHighThresholdValue())
			{
				return NEED_TO_BE_EXTENDED;
			}
			else
			{
				;//how to adjust the lowest parasitic packet service traffic  very important!!!!!!!!!!!!!!!!!!!!!!
			}				
		}
		else if(false)
		{
			return NEED_TO_BE_SHRINKED;
		}	
		else
			;
		return NOT_NEED_ADJUSTED;
	}
	
	public int howManyOTNAreNeedForthis(int _bw)
	{
		int _chushu = 1000;
		if(bod_on)
		{
			if(this.vtl_priority == PRIORITY_HIGH)
				_chushu = 750;
			else if(this.vtl_priority == PRIORITY_MID)
				_chushu = 850;
		}
		if((_bw%_chushu) > 0)
			return (_bw/_chushu) + 1;
		else
			return _bw/_chushu;
	}
*/	
	public double getPathLong()
	{
		if(relevantOTNServices.size()>0)
		{
			return relevantOTNServices.get(0).osBelongTo.path.get_weight() + 5;
		}
		else if(relevantOpticalServices.size()>0)
		{
			return relevantOpticalServices.get(0).path.get_weight() + 5;
		}
		else
			return -1.0;
	}
	
	public String toString()
	{
		String describtion = new String();
		describtion += "The vtl id:"+this.id;
		describtion += "\tthe source:"+this.sourceVertex+"\t the dest:"+this.destVertex;
		describtion += "\tthe priority:"+this.vtl_priority;
		describtion += "\n\tCarried ps num:"+carriedPacketServices.size();
		
		int request_bw = 0;
		for(PacketService _ps : carriedPacketServices)
		{
			describtion += "\n\t\t ps id:"+_ps.id+"\t the request bw is:"+_ps.getCurrentOccupiedBw();
			if(_ps instanceof PacketServiceChild)
				describtion += "\t it is a ps child, his father id:"+((PacketServiceChild)_ps).ps_father.id;
			request_bw += _ps.getCurrentOccupiedBw();
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
				;//System.out.println("************************************************************");
			else if(relevantOTNServices.size() * OTNService.BW_1G < request_bw)
				System.out.println("!!!!!!!!!!!!!!!!!!!Bug");
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

	public void showMyself() {
		System.out.println(this);	
	}


}
