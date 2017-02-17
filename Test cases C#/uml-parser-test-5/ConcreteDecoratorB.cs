using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Uml_parser_TestCase5
{
    public class ConcreteDecoratorB : Decorator
    {
        private string addedState;
        public ConcreteDecoratorB(Component C)
            : base(C)
        {

        }
        public string operation()
        {
            addedState = base.operation();
            return addedBehavior(addedState);
        }

        private string addedBehavior(string ln)
        {
            return "<h1>" + addedState + "</h1>";
        }
    }
}
