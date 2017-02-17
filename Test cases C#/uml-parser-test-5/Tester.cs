using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

public class Tester
{
	public static void Main(string[] args)
	{
		Component obj = new ConcreteDecoratorB(new ConcreteDecoratorA(new ConcreteComponent()));
		string result = obj.operation();
		Console.WriteLine(result);
	}
}

