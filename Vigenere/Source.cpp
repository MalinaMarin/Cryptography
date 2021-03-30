#include <iostream>
#include <fstream>
#include <string>
#include <algorithm>
using namespace std;

string mainCiphertext;
string mainPlaintext;
int mainKeyLength;
string mainKey;
const char alphabet[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
string inputFile = "input.txt";
string outputFile = "output.txt";
float frecvEngl[] =
{
	8.167, 1.492, 2.782, 4.253, 12.702, 2.228, 2.015, 6.094, 6.966, 0.153, 0.772, 4.025, 2.406, 6.749,
	7.507, 1.929, 0.095, 5.987, 6.327, 9.056, 2.758, 0.978, 2.360, 0.150, 1.974, 0.074
};
string readAndFilterInputFile()
{
	string text;
	ifstream fin;
	fin.open(inputFile);
	if (fin.is_open())
	{
		string line;
		while (getline(fin, line))
		{
			std::transform(line.begin(), line.end(), line.begin(), ::toupper);
			line.erase(remove_if(line.begin(), line.end(), [](char c) { return !isalpha(c); }), line.end());
			text = text + line;
		}

		fin.close();
	}
	return text;
}

void writeOutputFile()
{
	//clear the file
	std::ofstream fout;
	fout.open(outputFile, ios::trunc);

	fout << "Cipher text" << "\n";
	fout << "---------------------------------------------------\n";
	fout << mainCiphertext << "\n";
	fout << "\n\n";

	fout << "Key" << "\n";
	fout << "---------------------------------------------------\n";
	fout << mainKey << "\n";
	fout << "---------------------------------------------------\n\n";

	fout << "Plain text" << "\n";
	fout << "---------------------------------------------------\n";
	fout << mainPlaintext << "\n";
	fout << "---------------------------------------------------\n\n";

	cout << "You can open " << outputFile << " to see the result!\n";
	fout.close();
}

pair<int*, int> getFrequency(string cipher, int keyLength, int* frequency, int startPos)
{
	int lengthFrequencyJump = 0;
	for (int i = startPos; i < cipher.length(); i += keyLength)
	{
		int index = cipher[i] - 'A';
		frequency[index]++;
		lengthFrequencyJump++;
	}

	return make_pair(frequency, lengthFrequencyJump);
}

float IC(string cipher, int jump, int start) {
	float IC = 0;
	int lettersFrequency[26] = { 0 };
	
	pair<int*, int> freqJump = getFrequency(cipher, jump, lettersFrequency, start);

	for (int i = 0; i < 26; i++) {
		IC += ((freqJump.first[i] / (float)freqJump.second) *
			((freqJump.first[i] - 1) / ((float)freqJump.second - 1)));
	}

	return IC;
}


string shiftedText;
void SHIFT(string text, int x) {
	for (int i = 0; i < text.length(); i++) {
		shiftedText[i] = alphabet[(text[i] - 'A' + x) % 26];
	}
}


int getKeyLength(string cipher)
{
	float average_IC;
	float actual_IC = 0;
	float minDiff = 0;
	int keyLengthFound;
	for (int keyLength = 1; keyLength <= 100; keyLength++) {
		int frequency[26] = { 0 };
		pair<int*, int> freqJump = getFrequency(cipher, keyLength, frequency, 0);
		float sum = 0;
		for(int j = 0; j < keyLength; j++) {
			sum += IC(cipher, keyLength, j);
		}
		average_IC = sum / keyLength;
		if (actual_IC == 0 || (abs(0.065 - average_IC) < minDiff)) {
			actual_IC = average_IC;
			keyLengthFound = keyLength;
			minDiff = abs(0.065 - actual_IC);
		}
		
	}
	return keyLengthFound;
}

string getKey(string cipher, int keyLength)
{
	float MIC;
	int x;
	string key;
	for (int j = 0; j < keyLength; j++)
	{
		int frequency[26] = { 0 };
		pair<int*, int> freqJump = getFrequency(cipher, keyLength, frequency, j);
		x = -1;
		double MIC = 0;
		do {
			x++;
			MIC = 0;
			for (int i = 0; i < 26; i++) {
				MIC += (double)(frecvEngl[i] / 100) * (double)(freqJump.first[(i + x) % 26] / freqJump.second);
			}

		} while (!(MIC > 0.050 && MIC < 0.072));
		key += 65 + x;
	}

	return key;
}

char decryptAChar(char cipheredChar, char key)
{
	int keyPosition = key - 'A';
	int cipherCharPosition = cipheredChar - 'A';
	int plainCharPosition = ((cipherCharPosition - keyPosition) + 26) % 26;
	char plainChar = alphabet[plainCharPosition];
	return plainChar;
}

string decryptedText(string cipherText, string key)
{
	int i;
	int keyIndex = 0;
	string plainText = cipherText;

	for (i = 0;  i < cipherText.length(); i++)
	{
		plainText[i] = decryptAChar(cipherText[i], key[keyIndex]);
		keyIndex = (keyIndex + 1) % key.length();
	}

	return plainText;
}

void decrypt()
{
	cout << "Entering decrytion mode\n";
	cout << "copy cipher text into " << inputFile << "\n";
	system("pause");

	mainCiphertext = readAndFilterInputFile();
	if (mainCiphertext.length() == 0)
	{
		cout << "No input text!\n";
		return;
	}

	mainKeyLength = getKeyLength(mainCiphertext);
	if (mainKeyLength == 0)
	{
		cout << "Couldn't find key length";
		system("pause");
		return;
	}

	mainKey = getKey(mainCiphertext, mainKeyLength);
	if (mainKey == "")
	{
		cout << "Couldn't guess key, so I can't decrypt";
		system("pause");
		return;
	}

	mainPlaintext = decryptedText(mainCiphertext, mainKey);
	writeOutputFile();
}
string encrypt(string plainText, string key)
{
	string cipherText;
	for (int i = 0; i < (int)plainText.length(); i++)
	{
		int cipherPos = ((plainText[i] - 'A') + (key[i % key.length()] - 'A')) % 26;
		cipherText += alphabet[cipherPos];
	}
	return cipherText;
}
void encrypt()
{
	cout << "You chose to encrypt a text..\n";

	system("pause");

	mainPlaintext = readAndFilterInputFile();
	if (mainPlaintext.length() == 0)
	{
		cout << "Empty file!\n";
		return;
	}
	cout << "Type the key: ";
	cin >> mainKey;
	mainKeyLength = mainKey.length();
	for (int i = 0; i < mainKeyLength; i++) {
		mainKey[i] = toupper(mainKey[i]);
	}
	mainCiphertext = encrypt(mainPlaintext, mainKey);

	writeOutputFile();
}

int main()
{
	int option;
	do
	{
		cout << "\nPlease select option to encrypt a text(1) or decrypt a text(2)\n";
		cin >>option;
		if (option == 1)
		{
			encrypt();
		}
		else if (option == 2)
		{
			decrypt();
		}
		else
		{
			cout << "Invalid option... ";
			break;
		}
	} while (option != 1 && option != 2);

	system("pause");
	return 0;
}