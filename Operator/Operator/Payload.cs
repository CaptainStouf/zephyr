namespace Operator
{
    public class Payload
    {
        public Payload() { }

        public Payload(string title, string text)
        {
            this.id = 0;
            this.device = "zephyr-desktop-client-OperatorPlugin";
            this.title = title;
            this.text = text;
        }

        public int id;

        public string title;

        public string text;

        public string device;
    }
}