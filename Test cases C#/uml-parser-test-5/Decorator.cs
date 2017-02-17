using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Uml_parser_TestCase5
{
    public class Decorator : Component
    {
        private Component component;
        public Decorator(Component C)
        {
            component = C;
        }

        public string operation()
        {
            return component.operation();
        }
    }
}
