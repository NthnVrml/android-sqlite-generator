package com.trikke.util;

import com.trikke.data.*;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by the awesome :
 * User: trikke
 * Date: 16/10/13
 * Time: 21:04
 */
public class SqlUtil
{
	public static String URI( SQLObject obj )
	{
		return obj.name.toUpperCase() + "_URI";
	}

	public static String IDENTIFIER( SQLObject obj )
	{
		return obj.name.toUpperCase() + "_TABLE";
	}

	public static String ROW_COLUMN( SQLObject obj, Field row )
	{
		return obj.name.toUpperCase() + "_" + row.name.toUpperCase() + "_COLUMN";
	}

	public static String ROW_COLUMN_POSITION( SQLObject obj, Field row )
	{
		return obj.name.toUpperCase() + "_" + row.name.toUpperCase() + "_COLUMN_POSITION";
	}

	public static String ROW_COLUMN( SQLObject obj, String selector )
	{
		return obj.name.toUpperCase() + "_" + Util.sanitize( selector, false ).toUpperCase() + "_COLUMN";
	}

	public static String ROW_COLUMN_POSITION( SQLObject obj, String selector )
	{
		return obj.name.toUpperCase() + "_" + Util.sanitize( selector, false ).toUpperCase() + "_COLUMN_POSITION";
	}

	public static String generateCreateStatement( Model model, Table table )
	{
		String statement = "CREATE TABLE " + table.name + " (\" + \n";

		// default android row
		if ( !table.hasPrimaryKey() )
		{
			statement += "\t\t\t \"" + Table.ANDROID_ID + " integer primary key autoincrement,\" + \n";
		}

		Iterator<Field> fieldsiter = table.fields.iterator();

		while ( fieldsiter.hasNext() )
		{
			Field row = fieldsiter.next();

			statement += "\t\t\t " + model.getContractName() + "." + ROW_COLUMN( table, row ) + " + \" " + SqlUtil.getSQLtypeFor( row.type );

			if ( !row.constraints.isEmpty() )
			{
				Iterator<Constraint> constraintiter = row.constraints.iterator();
				while ( constraintiter.hasNext() )
				{
					statement += " " + constraintiter.next().value;
				}
			}

			if ( fieldsiter.hasNext() || !table.constraints.isEmpty() )
			{
				statement += ",\" + \n";
			}
		}

		Iterator<Constraint> constraintiter = table.constraints.iterator();

		while ( constraintiter.hasNext() )
		{
			Constraint constraint = constraintiter.next();
			statement += "\t\t\t \"CONSTRAINT " + constraint.name + " " + constraint.value;
			if ( constraintiter.hasNext() )
			{
				statement += ",\" + \n";
			}
		}

		statement += ")";

		return statement;
	}

	public static String generateCreateStatement( View view )
	{
		String statement;

		statement = "CREATE VIEW " + view.name + " AS \" +\n";
		statement += "\t\t\t\"SELECT \" +\n";
		Iterator<Pair<String, String>> iterator = view.fields.iterator();
		Pair<String, String> select;
		while ( iterator.hasNext() )
		{
			select = iterator.next();
			statement += "\t\t\t\t\"" + select.fst + " AS " + Util.sanitize( select.snd, false );
			if ( iterator.hasNext() )
			{
				statement += ", ";
			}
			statement += "\"+\n";
		}
		statement += "\t\t\t\" FROM " + view.getFromtables().get( 0 ) + "\" + \n";

		int i;
		String tablename;
		for ( i = 1; i < view.getFromtables().size(); i++ )
		{
			tablename = view.getFromtables().get( i );
			if (view.getJoinonfields().isEmpty())
			{
				statement += "\t\t\t\t\", " + tablename;
			}
			else
			{
				statement += "\t\t\t\t\" " + view.jointype.toUpperCase() + " " + tablename + " ON ";
				if ( i <= view.getJoinonfields().size() )
				{
					statement += view.getFromtables().get( 0 ) + "." + view.getJoinonfields().get( i - 1 ) + " = " + tablename + "." + view.getJoinonfields().get( i - 1 );
				}
			}
			if ( i < view.getFromtables().size() - 1 )
			{
				statement += "\" +\n";
			}
		}

		if ( !view.getGroupfields().isEmpty() )
		{
			statement += "\" +\n\t\t\t\" GROUP BY ";
		}

		Iterator<String> groupiterator = view.getGroupfields().iterator();
		while ( groupiterator.hasNext() )
		{
			statement += groupiterator.next();
			if ( iterator.hasNext() )
			{
				statement += ", ";
			}
		}

		if ( !view.getOrderfields().isEmpty() )
		{
			statement += "\" +\n\t\t\t\" ORDER BY ";
		}

		Iterator it = view.getOrderfields().entrySet().iterator();
		while ( it.hasNext() )
		{
			Map.Entry entry = (Map.Entry) it.next();

			if ( entry.getValue() == null )
			{
				statement += entry.getKey();
			} else
			{
				statement += entry.getKey() + " " + entry.getValue();
			}

			if ( it.hasNext() )
			{
				statement += ", ";
			}
		}

		return statement;
	}

	public static String[] getFieldsFromConstraint( Constraint constraint )
	{
		Pattern p = Pattern.compile( "\\((.*?)\\)" );
		Matcher m = p.matcher( constraint.value );

		String[] full = new String[]{};

		while ( m.find() )
		{
			full = Util.merge( full, m.group( 1 ).replaceAll( "[^A-Za-z0-9_,]", "" ).split( "," ) );
		}

		return full;
	}

	public static String getSQLtypeFor( String type )
	{
		type = Util.sanitize( type, false ).toLowerCase();
		if ( type.equals( "date" ) )
		{
			return "integer";
		}
		if ( type.equals( "float" ) )
		{
			return "float";
		}
		if ( type.equals( "double" ) )
		{
			return "real";
		}
		if ( type.equals( "long" ) )
		{
			return "integer";
		}
		if ( type.equals( "int" ) )
		{
			return "integer";
		}
		if ( type.equals( "boolean" ) )
		{
			return "boolean";
		}
		if ( type.equals( "string" ) )
		{
			return "text";
		}
		if ( type.equals( "null" ) )
		{
			return "null";
		}
		if ( type.equals( "integer" ) )
		{
			return "integer";
		}
		if ( type.equals( "real" ) )
		{
			return "real";
		}
		if ( type.equals( "text" ) )
		{
			return "text";
		}
		// special case
		if ( type.equals( "autoincrement" ) )
		{
			return "integer";
		}
		// fallback to blob
		return "blob";
	}

	public static String getJavaTypeFor( String type )
	{
		String tocheck = Util.sanitize( type, false ).toLowerCase();
		if ( tocheck.equals( "null" ) )
		{
			return "null";
		}
		if ( tocheck.equals( "integer" ) )
		{
			return "int";
		}
		if ( tocheck.equals( "real" ) )
		{
			return "double";
		}
		if ( tocheck.equals( "text" ) )
		{
			return "String";
		}
		if ( tocheck.equals( "string" ) )
		{
			return "String";
		}
		if ( tocheck.equals( "autoincrement" ) )
		{
			return "int";
		}
		// fallback to whatever it is
		return type;
	}
}
