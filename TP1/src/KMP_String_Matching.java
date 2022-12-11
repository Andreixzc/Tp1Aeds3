// JAVA program for implementation of KMP padraotern
// searching algorithm

class KMP_String_Matching {
	void KMPSearch(String padrao, String texto) {
		int M = padrao.length();
		int N = texto.length();

		// create lps[] that will hold the longest
		// prefix suffix values for padraotern
		int lps[] = new int[M];
		int j = 0; // index for padrao[]

		// Preprocess the padraotern (calculate lps[]
		// array)
		computeLPSArray(padrao, M, lps);

		int i = 0; // index for texto[]
		while ((N - i) >= (M - j)) {
			if (padrao.charAt(j) == texto.charAt(i)) {
				j++;
				i++;
			}
			if (j == M) {
				System.out.println("Padrao encontrado " + "no indice " + (i - j));
				j = lps[j - 1];
			}

			// mismatch after j matches
			else if (i < N && padrao.charAt(j) != texto.charAt(i)) {
				// Do not match lps[0..lps[j-1]] characters,
				// they will match anyway
				if (j != 0)
					j = lps[j - 1];
				else
					i = i + 1;
			}
		}
	}

	void computeLPSArray(String padrao, int M, int lps[]) {
		// length of the previous longest prefix suffix
		int len = 0;
		int i = 1;
		lps[0] = 0; // lps[0] is always 0

		// the loop calculates lps[i] for i = 1 to M-1
		while (i < M) {
			if (padrao.charAt(i) == padrao.charAt(len)) {
				len++;
				lps[i] = len;
				i++;
			} else // (padrao[i] != padrao[len])
			{
				// This is tricky. Consider the example.
				// AAACAAAA and i = 7. The idea is similar
				// to search step.
				if (len != 0) {
					len = lps[len - 1];

					// Also, note that we do not increment
					// i here
				} else // if (len == 0)
				{
					lps[i] = len;
					i++;
				}
			}
		}
	}

}
