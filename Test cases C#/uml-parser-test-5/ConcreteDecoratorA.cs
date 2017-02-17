using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Uml_parser_TestCase5
{
    public class ConcreteDecoratorA:Decorator
    {
        private string addedState;
        public ConcreteDecoratorA(Component C):base(C)
        {

        }
        public string operation()
        {
            addedState = base.operation();
            return addedBehavior(addedState);
        }
        
        private string addedBehavior(string i)
        {
            return "<em>" + addedState + "</em>";
        }

    }
}
