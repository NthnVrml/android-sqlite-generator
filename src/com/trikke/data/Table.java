package com.trikke.data;

import com.trikke.util.SqlUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by the awesome :
 * User: trikke
 * Date: 16/10/13
 * Time: 16:58
 */
public class Table extends SQLObject
{
	public static final String ANDROID_ID = "_id";

	public ArrayList<Field> fields = new ArrayList<Field>();
	public ArrayList<Constraint> constraints = new ArrayList<Constraint>();

	private boolean hasPrimaryKey;

	private Field primaryKey = null;

	public Table()
	{

	}

	public void addField( String type, String name, List<Constraint> constraints )
	{
		fields.add( new Field( type, name, constraints ) );
	}

	public void addConstraint( String name, String definition )
	{
		constraints.add( new Constraint( name, definition ) );
	}

	public String getSingleName()
	{
		return "SINGLE" + name.toUpperCase();
	}

	public String getAllName()
	{
		return "ALL" + name.toUpperCase();
	}

	public Field getFieldByName( String name )
	{
		for ( Field field : fields )
		{
			if ( field.name.toLowerCase().equals( name.toLowerCase() ) )
			{
				return field;
			}
		}
		return null;
	}

	public Field getPrimaryKey()
	{
		if ( hasPrimaryKey )
		{
			return primaryKey;
		}
		return getDefaultAndroidIdField();
	}

	public boolean hasPrimaryKey()
	{
		return hasPrimaryKey;
	}

	public void setPrimaryKey( String type, String name )
	{
		this.hasPrimaryKey = true;
		primaryKey = new Field( SqlUtil.getJavaTypeFor( type ), name, new ArrayList<Constraint>() );
	}

	public void setPrimaryKey( Field field )
	{
		this.hasPrimaryKey = true;
		primaryKey = field;
	}

	public static Field getDefaultAndroidIdField()
	{
		return new Field( "int", ANDROID_ID, new ArrayList<Constraint>() );
	}
}
