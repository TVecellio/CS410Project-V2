from openai import OpenAI

api_key = "insert API KEY HERE"

client = OpenAI(api_key=api_key)
stream = client.chat.completions.create(
    model="gpt-4",
    messages=[{"role": "user", "content": "Please respond with only the python version of this java program and no explanation please: "
                                          "	public class Test{"
                                          "public static void main(String[] args){"
                                          "int[] myArray = new int[10];"
                                          "int a = 3;"
                                          "int l = 14;"
                                          "a = a + l;"
                                          "int y = a;"
                                          "System.out.println();"
                                          "while(a < l){"
                                          "int a = a+1;"
                                          "}"
                                          "}"
                                          "}"}],
    stream=True,
)
for chunk in stream:
    if chunk.choices[0].delta.content is not None:
        print(chunk.choices[0].delta.content, end="")
