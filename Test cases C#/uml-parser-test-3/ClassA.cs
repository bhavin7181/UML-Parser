using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Uml_parser_TestCase3
{
    class ClassA
    {
        private string message;
        private string bark;
        protected string foo;
        protected string bar;
        string test;

        public string getMessage
        {
            get
            {
                return this.message;
            }
        }
        public string setMessage
        {
            set
            {
                this.message = value;
            }
        }
        public void testMethod()
        {
        }

    }
}
