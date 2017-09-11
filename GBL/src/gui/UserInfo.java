package gui;

public class UserInfo {
	
	public int userID;
	public int siteID;
	public char[] pswd;
	
	public UserInfo(int _userID, int _siteID, char[] _pswd)
	{
		userID = _userID;
		siteID = _siteID;
		pswd = _pswd;
	}

	public UserInfo()
	{
		
	}
}
