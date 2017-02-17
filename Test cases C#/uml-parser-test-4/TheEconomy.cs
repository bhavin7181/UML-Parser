using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Uml_parser_TestCase4
{
    public class TheEconomy:ConcreteSubject
    {
        public TheEconomy()
        {
            base.setState("The price of gallon is at $5.00/gal");
        }
    }
}
