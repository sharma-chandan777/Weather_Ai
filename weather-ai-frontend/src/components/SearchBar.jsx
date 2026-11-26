import { useState } from "react";

function SearchBar({ onSearch, isLoading = false }) {
  const [value, setValue] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();

    const query = value.trim();
    if (!query || isLoading) return;

    onSearch(query);
    setValue("");
  };

  return (
    <form className="search-box" onSubmit={handleSubmit}>
      <span className="search-icon">⌕</span>

      <input
        type="text"
        placeholder="Search city..."
        value={value}
        disabled={isLoading}
        onChange={(e) => setValue(e.target.value)}
      />

      <button type="submit" disabled={isLoading || !value.trim()}>
        {isLoading ? "Searching..." : "Search"}
      </button>
    </form>
  );
}

export default SearchBar;