using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Uml_parser_TestCase3
{
    class ClassB : ClassA
    {
        private string hello;
        private string getA()
        {
            return hello;
        }

        private string setA(string value)
        {
			this.hello = value;
        }
    }
}
